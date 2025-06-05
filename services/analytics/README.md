# Analytics Service
_Microservice used to analyze survey results._


## tests

```bash
pip install -e .[test]
pytest -q
```

# requirements

The runtime dependencies are defined in `pyproject.toml`. To generate a
`requirements.txt` file, install `pip-tools` and run:

```bash
pip install pip-tools
pip-compile --output-file=requirements.txt pyproject.toml
```

