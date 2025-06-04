from fastapi import FastAPI
from pydantic import BaseModel
from typing import List

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
        if q.averageScore >= 4:
            summary = "Most respondents agree"
        elif q.averageScore <= 2:
            summary = "Most respondents disagree"
        else:
            summary = "Mixed opinions"
        insights.append(QuestionInsight(questionId=q.questionId, summary=summary))
    return Insights(surveyId=result.surveyId, insights=insights)
