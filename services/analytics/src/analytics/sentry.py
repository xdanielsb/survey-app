from __future__ import annotations

import os
from typing import Optional

import sentry_sdk
from fastapi import FastAPI
from sentry_sdk.integrations.asgi import SentryAsgiMiddleware

SENTRY_AUTH_DSN: Optional[str] = os.getenv("SENTRY_AUTH_DSN")


def init_sentry(app: FastAPI) -> None:
    if SENTRY_AUTH_DSN:
        sentry_sdk.init(dsn=SENTRY_AUTH_DSN, send_default_pii=True)
        app.add_middleware(SentryAsgiMiddleware)