FROM python:3.12-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY ./src /app/src
COPY ./pyproject.toml /app/
RUN pip install --no-cache-dir -e .

ARG SENTRY_AUTH_DSN
ENV SENTRY_AUTH_DSN=$SENTRY_AUTH_DSN

CMD ["uvicorn", "analytics.main:app", "--host", "0.0.0.0", "--port", "8000"]
