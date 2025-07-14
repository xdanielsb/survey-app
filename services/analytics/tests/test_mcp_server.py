from typing import Any, Sequence, cast

import anyio
from mcp.types import ContentBlock, TextContent

from analytics import mcp_server


def test_summarize_tool(monkeypatch):
    def fake_summary(prompt: str) -> str:
        return f"echo:{prompt}"

    monkeypatch.setattr(mcp_server.ollama_client, "summarize", fake_summary)
    result = anyio.run(mcp_server.server.call_tool, "summarize", {"question": "hi"})
    content, _ = cast(tuple[Sequence[ContentBlock], dict[str, Any]], result)
    first = cast(TextContent, content[0])
    assert first.text == "echo:hi"
