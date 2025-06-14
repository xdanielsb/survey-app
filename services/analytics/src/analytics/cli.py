import logging
import os

import click

from analytics.exceptions import AnalyticException
from analytics.ollama import OllamaClient

DEFAULT_URL = os.getenv("OLLAMA_URL", "http://localhost:11434")
DEFAULT_MODEL = os.getenv("OLLAMA_MODEL", "tinyllama")

logger = logging.getLogger("analytics.cli")
logger.setLevel(logging.INFO)
_handler = logging.StreamHandler()
_handler.setFormatter(logging.Formatter("%(levelname)s: %(message)s"))
logger.addHandler(_handler)


@click.group()
@click.option(
    "--url",
    default=DEFAULT_URL,
    envvar="OLLAMA_URL",
    show_default=True,
    help="Base URL of the Ollama server.",
)
@click.option(
    "--model",
    default=DEFAULT_MODEL,
    envvar="OLLAMA_MODEL",
    show_default=True,
    help="Model to use when communicating with the server.",
)
@click.pass_context
def cli(ctx: click.Context, url: str, model: str) -> None:
    ctx.obj = {"client": OllamaClient(url, model, logger=logger)}


@cli.command()
@click.argument("prompt")
@click.pass_context
def ask(ctx: click.Context, prompt: str) -> None:
    client: OllamaClient = ctx.obj["client"]
    try:
        answer = client.summarize(prompt)
    except AnalyticException as exc:  # noqa: BLE001
        raise click.ClickException(str(exc)) from exc

    click.echo(answer)


@cli.command(name="pull-model")
@click.pass_context
def pull_model(ctx: click.Context) -> None:
    client: OllamaClient = ctx.obj["client"]
    try:
        client._pull()
    except AnalyticException as exc:  # noqa: BLE001
        raise click.ClickException(str(exc)) from exc
    click.echo(f"Model '{client.model}' pulled successfully.")


if __name__ == "__main__":  # pragma: no cover
    cli()