import logging
import os
from typing import Optional

import sentry_sdk
from fastapi import FastAPI
from logstash import LogstashFormatterVersion1, TCPLogstashHandler
from sentry_sdk.integrations.asgi import SentryAsgiMiddleware

from analytics.models import ChatRequest, ChatResponse
from analytics.ollama import OllamaClient

OLLAMA_URL: str = os.getenv("OLLAMA_URL", "http://localhost:11434")
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
except Exception as exc:  # noqa: BLE001
    logger.warning("Failed to configure Logstash handler: %s", exc)

ollama_client: OllamaClient = OllamaClient(OLLAMA_URL, OLLAMA_MODEL, logger=logger)


app = FastAPI()
if SENTRY_AUTH_DSN:
    app.add_middleware(SentryAsgiMiddleware)


@app.post("/ask", response_model=ChatResponse)
def ask(request: ChatRequest) -> ChatResponse:
    answer: Optional[str] = None
    try:
        answer = ollama_client.summarize(request.question)
    except Exception:  # noqa: BLE001
        answer = None

    if not answer:
        answer = f"Received question: {request.question}"

    return ChatResponse(answer=answer)