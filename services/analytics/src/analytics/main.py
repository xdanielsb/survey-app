from __future__ import annotations

import os
from typing import Optional

from fastapi import FastAPI

from analytics.exceptions import AnalyticException
from analytics.logger import get_logger
from analytics.models import ChatRequest, ChatResponse
from analytics.ollama import OllamaClient
from analytics.sentry import init_sentry

OLLAMA_URL: str = os.getenv("OLLAMA_URL", "http://localhost:11434")
OLLAMA_MODEL: str = "tinyllama"

logger = get_logger("analytics")
ollama_client: OllamaClient = OllamaClient(OLLAMA_URL, OLLAMA_MODEL, logger=logger)

app = FastAPI()
init_sentry(app)


@app.post("/ask", response_model=ChatResponse)
def ask(request: ChatRequest) -> ChatResponse:
    answer: Optional[str] = None
    try:
        answer = ollama_client.summarize(request.question)
    except AnalyticException as exc:
        logger.error("Analytics error", exc_info=exc)

    if not answer:
        answer = f"Received question: {request.question}"

    return ChatResponse(answer=answer)