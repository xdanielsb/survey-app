from click.testing import CliRunner

from analytics.cli import cli


def test_ask_command(monkeypatch):
    def fake_summarize(self, prompt: str) -> str:  # noqa: D401
        return f"echo:{prompt}"

    monkeypatch.setattr("analytics.cli.OllamaClient.summarize", fake_summarize)
    runner = CliRunner()
    result = runner.invoke(cli, ["ask", "hello"])
    assert result.exit_code == 0
    assert "echo:hello" in result.output


def test_pull_model_command(monkeypatch):
    called = False

    def fake_pull(self) -> None:  # noqa: D401
        nonlocal called
        called = True

    monkeypatch.setattr("analytics.cli.OllamaClient._pull", fake_pull)
    runner = CliRunner()
    result = runner.invoke(cli, ["pull-model"])
    assert result.exit_code == 0
    assert called