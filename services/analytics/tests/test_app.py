
import pytest
from fastapi.testclient import TestClient

from app import app

client = TestClient(app)

test_cases = [
    (4.5, "Most respondents agree"),
    (1.0, "Most respondents disagree"),
    (3.0, "Mixed opinions"),
]


@pytest.mark.parametrize("score,expected", test_cases)
def test_analyze_summary(score, expected):
    payload = {
        "surveyId": 1,
        "surveyTitle": "Demo",
        "questionResults": [
            {"questionId": 1, "questionText": "Q", "totalResponses": 1, "averageScore": score}
        ],
    }
    response = client.post("/analyze", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["surveyId"] == 1
    assert data["insights"][0]["summary"] == expected
