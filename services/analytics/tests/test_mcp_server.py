import anyio

from analytics import mcp_server


def test_summarize_tool(monkeypatch):
    def fake_summary(prompt: str) -> str:
        return f"echo:{prompt}"

    monkeypatch.setattr(mcp_server.ollama_client, "summarize", fake_summary)
    result = anyio.run(mcp_server.server.call_tool, "summarize", {"question": "hi"})
    content, data = result
    assert content[0].text == "echo:hi"