class AnalyticException(Exception):
    """Base exception for the analytics service."""


class OllamaUnreachable(AnalyticException):
    """Raised when Ollama cannot be reached after several attempts."""


class ModelPullError(AnalyticException):
    """Raised when pulling a model from Ollama fails."""


class OllamaRequestError(AnalyticException):
    """Raised when an Ollama request fails."""