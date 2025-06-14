package com.survey.backend.exception;

import com.stripe.exception.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
    AccessDeniedException.class,
    SecurityException.class,
    AuthenticationException.class,
    Unauthorized.class
  })
  public ResponseEntity<ApiError> onAuth(Throwable ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ApiError("AUTH_ERROR", ex.getMessage()));
  }

  @ExceptionHandler(ExpiredTokenException.class)
  public ResponseEntity<ApiError> handleExpired(ExpiredTokenException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError("TOKEN_EXPIRED", ex.getMessage()));
  }

  @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
  public ResponseEntity<ApiError> handleNoCredentials(
      AuthenticationCredentialsNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError("AUTH_ERROR", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> onServerError(Exception ex) {
    if (ex instanceof ResponseStatusException) {
      ResponseStatusException statusEx = (ResponseStatusException) ex;
      return ResponseEntity.status(statusEx.getStatusCode())
          .body(new ApiError("SERVER_ERROR", statusEx.getReason()));
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiError("SERVER_ERROR", ex.getMessage()));
    }
  }
}
