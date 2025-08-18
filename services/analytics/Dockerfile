FROM python:3.13.7-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install uv && uv pip install --system -r requirements.txt
COPY ./src /app/src
COPY ./pyproject.toml /app/
RUN uv pip install --system -e .

ARG SENTRY_AUTH_DSN
ENV SENTRY_AUTH_DSN=$SENTRY_AUTH_DSN

ARG ENVIRONMENT
ENV ENVIRONMENT=$ENVIRONMENT

CMD ["uvicorn", "analytics.main:app", "--host", "0.0.0.0", "--port", "8000"]
