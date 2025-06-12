from fastapi.testclient import TestClient

from app import app

client = TestClient(app)


def test_export_pdf():
    response = client.post("/export", json={"foo": "bar", "num": 1})
    assert response.status_code == 200
    assert response.headers["content-type"] == "application/pdf"
    assert response.content.startswith(b"%PDF")
