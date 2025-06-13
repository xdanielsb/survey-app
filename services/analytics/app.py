import os

import httpx
from fastapi import FastAPI
from pydantic import BaseModel

OLLAMA_URL = os.getenv("OLLAMA_URL")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "llama3")


def summarize_with_ollama(prompt: str) -> str:
    if not OLLAMA_URL:
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
        raise RuntimeError(f"ollama request failed: {exc}")


app = FastAPI()


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