package com.survey.backend.exception;

import com.stripe.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler({
    AccessDeniedException.class,
    SecurityException.class,
    AuthenticationException.class,
  })
  public ResponseEntity<ApiError> onAuth(Throwable ex) {
    log.error(
        "Global handler: Authentication error onAuth: {} - {}",
        ex.getClass().getName(),
        ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ApiError("AUTH_ERROR", ex.getMessage()));
  }

  @ExceptionHandler(ExpiredTokenException.class)
  public ResponseEntity<ApiError> handleExpired(ExpiredTokenException ex) {
    log.error(
        "Expired token error handle expired: {} - {}", ex.getClass().getName(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError("TOKEN_EXPIRED", ex.getMessage()));
  }

  @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
  public ResponseEntity<ApiError> handleNoCredentials(
      AuthenticationCredentialsNotFoundException ex) {
    log.error(
        "Global handler handleNotCredentials: Authentication credentials not found: {} - {}",
        ex.getClass().getName(),
        ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError("AUTH_ERROR", ex.getMessage()));
  }

  @ExceptionHandler(Unauthorized.class)
  public ResponseEntity<ApiError> handleHttpUnauthorized(Unauthorized ex) {
    log.error(
        "Global handler handleHttpUnauthorized: HTTP Unauthorized: {} - {}",
        ex.getClass().getName(),
        ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError("AUTH_ERROR", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> onServerError(Exception ex) {
    log.error(
        "Global handler: Server error onServerError: {} - {}",
        ex.getClass().getName(),
        ex.getMessage());
    if (ex instanceof ResponseStatusException statusEx) {
      return ResponseEntity.status(statusEx.getStatusCode())
          .body(new ApiError("SERVER_ERROR", statusEx.getReason()));
    } else if (ex instanceof HttpClientErrorException clientEx) {
      return ResponseEntity.status(clientEx.getStatusCode())
          .body(new ApiError("CLIENT_ERROR", clientEx.getResponseBodyAsString()));
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiError("SERVER_ERROR", ex.getMessage()));
    }
  }
}
