# Exports Service
_Microservice used to generate PDF files from JSON payloads._

## Tests
```bash
pip install -e .[test]
pytest -q
```

## requirements
To generate `requirements.txt` run:
```bash
pip install pip-tools
pip-compile --output-file=requirements.txt pyproject.toml
```
