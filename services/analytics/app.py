from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import os
import httpx


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


class QuestionResult(BaseModel):
    questionId: int
    questionText: str
    totalResponses: int
    averageScore: float

class SurveyResult(BaseModel):
    surveyId: int
    surveyTitle: str
    questionResults: List[QuestionResult]

class QuestionInsight(BaseModel):
    questionId: int
    summary: str

class Insights(BaseModel):
    surveyId: int
    insights: List[QuestionInsight]

app = FastAPI()

@app.post("/analyze", response_model=Insights)
def analyze(result: SurveyResult):
    insights = []
    for q in result.questionResults:
        summary = None
        if OLLAMA_URL:
            prompt = (
                f"Question: {q.questionText}\n"
                f"Average score: {q.averageScore} (1-5). "
                f"Summarize the sentiment in one short sentence."
            )
            try:
                summary = summarize_with_ollama(prompt)
            except Exception:
                summary = None

        if not summary:
            if q.averageScore >= 4:
                summary = "Most respondents agree"
            elif q.averageScore <= 2:
                summary = "Most respondents disagree"
            else:
                summary = "Mixed opinions"
        insights.append(QuestionInsight(questionId=q.questionId, summary=summary))
    return Insights(surveyId=result.surveyId, insights=insights)
