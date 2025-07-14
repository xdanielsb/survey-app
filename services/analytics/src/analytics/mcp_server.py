"""Minimal MCP server exposing a summary tool."""

import os

from mcp.server import FastMCP

from analytics.exceptions import AnalyticException
from analytics.logger import get_logger
from analytics.ollama import OllamaClient

logger = get_logger("mcp")

OLLAMA_URL: str = os.getenv("OLLAMA_URL", "http://localhost:11434")
OLLAMA_MODEL: str = "tinyllama"
ollama_client = OllamaClient(OLLAMA_URL, OLLAMA_MODEL, logger=logger)

server = FastMCP(name="analytics")


@server.tool(name="summarize")
def summarize(question: str) -> str:
    """Return a summary for ``question`` using Ollama."""
    try:
        return ollama_client.summarize(question)
    except AnalyticException as exc:  # noqa: BLE001
        logger.error("Failed to summarize", exc_info=exc)
        return f"Received question: {question}"


if __name__ == "__main__":  # pragma: no cover
    server.run("sse")