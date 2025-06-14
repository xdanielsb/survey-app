import logging
import os
import time
from typing import Optional

import httpx
import sentry_sdk
from fastapi import FastAPI
from logstash import LogstashFormatterVersion1, TCPLogstashHandler
from pydantic import BaseModel
from sentry_sdk.integrations.asgi import SentryAsgiMiddleware

OLLAMA_URL: Optional[str] = os.getenv("OLLAMA_URL")
OLLAMA_MODEL: str =  "tinyllama"
LOGSTASH_HOST: str = os.getenv("LOGSTASH_HOST", "logstash")
LOGSTASH_PORT: int = int(os.getenv("LOGSTASH_PORT", "5000"))
SENTRY_AUTH_DSN: Optional[str] = os.getenv("SENTRY_AUTH_DSN")

logger = logging.getLogger("analytics")
logger.setLevel(logging.INFO)

_stream_handler = logging.StreamHandler()
_stream_handler.setFormatter(logging.Formatter("%(levelname)s: %(message)s"))
logger.addHandler(_stream_handler)

if SENTRY_AUTH_DSN:
    sentry_sdk.init(dsn=SENTRY_AUTH_DSN, send_default_pii=True)

try:
    _logstash_handler = TCPLogstashHandler(LOGSTASH_HOST, LOGSTASH_PORT, version=1)
    _logstash_handler.setFormatter(LogstashFormatterVersion1())
    logger.addHandler(_logstash_handler)
except Exception as exc:
    logger.warning("Failed to configure Logstash handler: %s", exc)

def _ollama_generate(prompt: str, attempts: int = 4) -> httpx.Response:
    last_exc = None
    for attempt in range(1, attempts + 1):
        try:
            return httpx.post(
                f"{OLLAMA_URL}/api/generate",
                json={"model": OLLAMA_MODEL, "prompt": prompt, "stream": False},
                timeout=httpx.Timeout(300),
            )
        except httpx.ConnectError as exc:
            last_exc = exc
            wait = 2 ** (attempt - 1)
            logger.warning(f"Ollama not reachable (attempt {attempt}/{attempts}), retrying in {wait}s...")
            time.sleep(wait)
    raise RuntimeError(f"Ollama unreachable after {attempts} attempts: {last_exc}") from last_exc

def _ollama_pull() -> None:
    logger.info("Pulling model '%s' from Ollama…", OLLAMA_MODEL)
    resp = httpx.post(
        f"{OLLAMA_URL}/api/pull",
        json={"name": OLLAMA_MODEL},
        timeout=60,
    )
    resp.raise_for_status()
    logger.info("Model '%s' pulled successfully.", OLLAMA_MODEL)

def summarize_with_ollama(prompt: str) -> str:
    if not OLLAMA_URL:
        logger.error("OLLAMA_URL not configured")
        raise RuntimeError("OLLAMA_URL not configured")

    logger.info(
        "Summarizing with Ollama",
        extra={"prompt": prompt, "model": OLLAMA_MODEL, "url": OLLAMA_URL},
    )

    try:
        resp = _ollama_generate(prompt)
        needs_pull = (
            resp.status_code == 400
            and "not found" in resp.text.lower()
        )
        if not needs_pull:
            try:
                data = resp.json()
                needs_pull = "error" in data and "not found" in data["error"].lower()
            except ValueError:
                pass

        if needs_pull:
            logger.warning("Model '%s' not present on server; pulling now…", OLLAMA_MODEL)
            _ollama_pull()
            resp = _ollama_generate(prompt)

        resp.raise_for_status()
        data = resp.json()
        return data.get("response", "").strip()

    except Exception as exc:
        logger.error("Ollama request failed", exc_info=exc, extra={"prompt": prompt})
        raise RuntimeError(f"ollama request failed: {exc}") from exc

app = FastAPI()
if SENTRY_AUTH_DSN:
    app.add_middleware(SentryAsgiMiddleware)

class ChatRequest(BaseModel):
    question: str

class ChatResponse(BaseModel):
    answer: str

@app.post("/ask", response_model=ChatResponse)
def ask(request: ChatRequest):
    answer: Optional[str] = None

    if OLLAMA_URL:
        try:
            answer = summarize_with_ollama(request.question)
        except Exception:
            answer = None

    if not answer:
        answer = f"Received question: {request.question}"

    return ChatResponse(answer=answer)
