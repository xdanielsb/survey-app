from fastapi.testclient import TestClient

from app import app

client = TestClient(app)



def test_ask_endpoint_fallback():
    response = client.post("/ask", json={"question": "Hello"})
    assert response.status_code == 200
    assert response.json()["answer"].startswith("Received question")