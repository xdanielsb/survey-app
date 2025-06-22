import logging

import httpx
import pytest

from analytics.ollama import ModelPullError, OllamaClient, OllamaUnreachable


def make_client():
    logger = logging.getLogger("dummy")
    return OllamaClient("http://ollama", "model", logger=logger)


def test_needs_pull_variants():
    client = make_client()
    req = httpx.Request("POST", "http://ollama")

    resp_400 = httpx.Response(400, text="model not found", request=req)
    assert client._needs_pull(resp_400)

    resp_json_error = httpx.Response(200, json={"error": "Model NOT Found"}, request=req)
    assert client._needs_pull(resp_json_error)

    resp_json_ok = httpx.Response(200, json={"response": "ok"}, request=req)
    assert not client._needs_pull(resp_json_ok)

    resp_bad_json = httpx.Response(200, text="not json", request=req)
    assert not client._needs_pull(resp_bad_json)


def test_summarize_pulls_when_missing(monkeypatch):
    client = make_client()

    req = httpx.Request("POST", "http://ollama")
    responses = [
        httpx.Response(400, text="not found", request=req),
        httpx.Response(200, json={"response": "answer"}, request=req),
    ]

    def fake_generate(self, prompt):
        return responses.pop(0)

    pulled = {"count": 0}

    def fake_pull(self):
        pulled["count"] += 1

    monkeypatch.setattr(OllamaClient, "_generate", fake_generate)
    monkeypatch.setattr(OllamaClient, "_pull", fake_pull)

    result = client.summarize("hello")
    assert result == "answer"
    assert pulled["count"] == 1


def test_generate_retries_and_fails(monkeypatch):
    client = make_client()

    def fail_post(*args, **kwargs):
        raise httpx.ConnectError("boom", request=httpx.Request("POST", "http://ollama"))

    monkeypatch.setattr(httpx, "post", fail_post)
    monkeypatch.setattr("time.sleep", lambda *a, **k: None)

    with pytest.raises(OllamaUnreachable):
        client._generate("q", attempts=2)


def test_pull_raises_model_pull_error(monkeypatch):
    client = make_client()

    def post_error(*args, **kwargs):
        request = httpx.Request("POST", "http://ollama")
        return httpx.Response(400, text="bad", request=request)

    monkeypatch.setattr(httpx, "post", post_error)

    with pytest.raises(ModelPullError):
        client._pull()