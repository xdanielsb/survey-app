# Analytics Service

_AI microservice for summarizing survey results._

![Analytics CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-analytics.yml/badge.svg)
[![Coverage](https://img.shields.io/codecov/c/github/xdanielsb/survey-app?flag=analytics&label=coverage&branch=master)](https://app.codecov.io/gh/xdanielsb/survey-app/flags/analytics)

## Key Features

-  **FastAPI** with type hints and Pydantic models
-  **LLM-powered chat** at `/ask` via an Ollama backend
-  **CLI** tool for interacting with the service locally
-  **Sentry** integration for error tracking

## Development

```bash
uvicorn analytics.main:app --reload
```

## Tests

```bash
uv pip install --system -e .[test]
pytest -q
```

## Linting

```bash
ruff check . --fix
isort .
```

## Requirements

Runtime dependencies are defined in `pyproject.toml`. To generate a
`requirements.txt` file, install `pip-tools` and run:

```bash
pip install pip-tools
pip-compile --output-file=requirements.txt pyproject.toml
```
