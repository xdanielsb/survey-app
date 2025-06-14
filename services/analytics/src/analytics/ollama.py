from __future__ import annotations

import logging
import time
from typing import Optional

import httpx


class OllamaClient:
    """Client for interacting with an Ollama server."""

    def __init__(self, url: str, model: str, logger: Optional[logging.Logger] = None) -> None:
        self.url = url.rstrip("/")
        self.model = model
        self.logger = logger or logging.getLogger(self.__class__.__name__.lower())

    def _generate(self, prompt: str, attempts: int = 4) -> httpx.Response:
        last_exc = None
        for attempt in range(1, attempts + 1):
            try:
                return httpx.post(
                    f"{self.url}/api/generate",
                    json={"model": self.model, "prompt": prompt, "stream": False},
                    timeout=httpx.Timeout(300),
                )
            except httpx.ConnectError as exc:
                last_exc = exc
                wait = 2 ** (attempt - 1)
                self.logger.warning(
                    "Ollama not reachable (attempt %s/%s), retrying in %ss…",
                    attempt,
                    attempts,
                    wait,
                )
                time.sleep(wait)
        raise RuntimeError(f"Ollama unreachable after {attempts} attempts: {last_exc}") from last_exc

    def _pull(self) -> None:
        self.logger.info("Pulling model '%s' from Ollama…", self.model)
        resp = httpx.post(
            f"{self.url}/api/pull",
            json={"name": self.model},
            timeout=60,
        )
        resp.raise_for_status()
        self.logger.info("Model '%s' pulled successfully.", self.model)

    def summarize(self, prompt: str) -> str:
        """Generate a summary for ``prompt`` using Ollama."""
        self.logger.info(
            "Summarizing with Ollama",
            extra={"prompt": prompt, "model": self.model, "url": self.url},
        )

        try:
            resp = self._generate(prompt)
            needs_pull = resp.status_code == 400 and "not found" in resp.text.lower()
            if not needs_pull:
                try:
                    data = resp.json()
                    needs_pull = "error" in data and "not found" in data["error"].lower()
                except ValueError:
                    pass

            if needs_pull:
                self.logger.warning(
                    "Model '%s' not present on server; pulling now…", self.model
                )
                self._pull()
                resp = self._generate(prompt)

            resp.raise_for_status()
            data = resp.json()
            return data.get("response", "").strip()

        except Exception as exc:  # noqa: BLE001
            self.logger.error("Ollama request failed", exc_info=exc, extra={"prompt": prompt})
            raise RuntimeError(f"ollama request failed: {exc}") from exc