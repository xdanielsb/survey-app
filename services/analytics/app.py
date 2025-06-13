import logging
import os

import httpx
import sentry_sdk
from fastapi import FastAPI
from logstash import LogstashFormatterVersion1, TCPLogstashHandler
from pydantic import BaseModel
from sentry_sdk.integrations.asgi import SentryAsgiMiddleware

OLLAMA_URL = os.getenv("OLLAMA_URL")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "llama3")
LOGSTASH_HOST = os.getenv("LOGSTASH_HOST", "logstash")
LOGSTASH_PORT = int(os.getenv("LOGSTASH_PORT", "5000"))
SENTRY_AUTH_DSN = os.getenv("SENTRY_AUTH_DSN")

logger = logging.getLogger("analytics")
logger.setLevel(logging.INFO)
stream_handler = logging.StreamHandler()
stream_handler.setFormatter(logging.Formatter("%(levelname)s: %(message)s"))
logger.addHandler(stream_handler)

if SENTRY_AUTH_DSN:
    sentry_sdk.init(dsn=SENTRY_AUTH_DSN, send_default_pii=True)

try:
    handler = TCPLogstashHandler(LOGSTASH_HOST, LOGSTASH_PORT, version=1)
    handler.setFormatter(LogstashFormatterVersion1())
    logger.addHandler(handler)
except Exception as exc:  # pragma: no cover - optional logstash
    logger.warning("Failed to configure logstash handler: %s", exc)


def summarize_with_ollama(prompt: str) -> str:
    # log prompt and information
    logger.info("Summarizing with ollama", extra={"prompt": prompt, "model": OLLAMA_MODEL, "url": OLLAMA_URL})
    if not OLLAMA_URL:
        logger.error("OLLAMA_URL not configured")
        raise RuntimeError("OLLAMA_URL not configured")
    try:
        response = httpx.post(
            f"{OLLAMA_URL}/api/generate",
            json={"model": OLLAMA_MODEL, "prompt": prompt, "stream": False},
            timeout=10,
        )
        response.raise_for_status()
        data = response.json()
        return data.get("response", "").strip()
    except Exception as exc:
        logger.error("Ollama request failed", exc_info=exc, extra={"prompt": prompt})
        raise RuntimeError(f"ollama request failed: {exc}")


app = FastAPI()
if SENTRY_AUTH_DSN:
    app.add_middleware(SentryAsgiMiddleware)


class ChatRequest(BaseModel):
    question: str


class ChatResponse(BaseModel):
    answer: str

@app.post("/ask", response_model=ChatResponse)
def ask(request: ChatRequest):
    answer = None
    if OLLAMA_URL:
        try:
            answer = summarize_with_ollama(request.question)
        except Exception:
            answer = None
    if not answer:
        answer = f"Received question: {request.question}"
    return ChatResponse(answer=answer)