import logging
import os

from logstash import LogstashFormatterVersion1, TCPLogstashHandler

LOGSTASH_HOST: str = os.getenv("LOGSTASH_HOST", "logstash")
LOGSTASH_PORT: int = int(os.getenv("LOGSTASH_PORT", "5000"))
ENVIRONMENT: str = os.getenv("ENVIRONMENT", "unknown")


def get_logger(name: str = "analytics") -> logging.LoggerAdapter:
    logger = logging.getLogger(name)
    logger.setLevel(logging.INFO)

    if not logger.handlers:
        # Console handler
        stream_handler = logging.StreamHandler()
        stream_handler.setFormatter(logging.Formatter("%(levelname)s: %(message)s"))
        logger.addHandler(stream_handler)

        # Logstash handler
        try:
            logstash_handler = TCPLogstashHandler(LOGSTASH_HOST, LOGSTASH_PORT, version=1)
            logstash_handler.setFormatter(LogstashFormatterVersion1())
            logger.addHandler(logstash_handler)
        except Exception as exc:  # noqa: BLE001
            logger.warning("Failed to configure Logstash handler: %s", exc, exc_info=True)

    return logging.LoggerAdapter(logger, {"environment": ENVIRONMENT})
