from __future__ import annotations

import logging
import time
from typing import Optional

import httpx

from analytics.exceptions import (
    ModelPullError,
    OllamaRequestError,
    OllamaUnreachable,
)


class OllamaClient:
    """Client for interacting with an Ollama server."""

    MAX_ATTEMPTS = 4
    GENERATE_TIMEOUT = 300
    PULL_TIMEOUT = 60

    def __init__(self, url: str, model: str, logger: Optional[logging.Logger] = None) -> None:
        self.url = url.rstrip("/")
        self.model = model
        self.logger = logger or logging.getLogger(self.__class__.__name__.lower())

    def _generate(self, prompt: str, attempts: int = MAX_ATTEMPTS) -> httpx.Response:
        last_exc = None
        for attempt in range(1, attempts + 1):
            try:
                return httpx.post(
                    f"{self.url}/api/generate",
                    json={"model": self.model, "prompt": prompt, "stream": False},
                    timeout=httpx.Timeout(self.GENERATE_TIMEOUT),
                )
            except httpx.ConnectError as exc:
                last_exc = exc
                wait = 2 ** (attempt - 1)
                self.logger.warning(
                    f"Ollama not reachable (attempt {attempt}/{attempts}), retrying in {wait}s…"
                )
                time.sleep(wait)

        raise OllamaUnreachable(f"Ollama unreachable after {attempts} attempts") from last_exc

    def _pull(self) -> None:
        self.logger.info(f"Pulling model '{self.model}' from Ollama…")
        try:
            response = httpx.post(
                f"{self.url}/api/pull",
                json={"name": self.model},
                timeout=httpx.Timeout(self.PULL_TIMEOUT),
            )
            response.raise_for_status()
        except httpx.HTTPError as exc:  # noqa: BLE001
            raise ModelPullError(f"Failed to pull model '{self.model}': {exc}") from exc
        self.logger.info(f"Model '{self.model}' pulled successfully.")

    def _needs_pull(self, response: httpx.Response) -> bool:
        if response.status_code == 400 and "not found" in response.text.lower():
            return True
        try:
            data = response.json()
            return "error" in data and "not found" in data["error"].lower()
        except ValueError:
            return False

    def summarize(self, prompt: str) -> str:
        """Generate a summary for ``prompt`` using Ollama."""
        self.logger.info(
            "Summarizing with " + self.model,
            extra={"prompt": prompt, "model": self.model, "url": self.url},
        )

        try:
            response = self._generate(prompt)

            if self._needs_pull(response):
                self.logger.warning(f"Model '{self.model}' not present on server; pulling now…")
                self._pull()
                response = self._generate(prompt)

            response.raise_for_status()
            data = response.json()
            return data.get("response", "").strip()

        except Exception as exc:  # noqa: BLE001

            self.logger.error(
                "Ollama request failed",
                exc_info=exc,
                extra={"prompt": prompt, "model": self.model},
            )
            raise OllamaRequestError(f"Ollama request failed: {exc}") from exc
