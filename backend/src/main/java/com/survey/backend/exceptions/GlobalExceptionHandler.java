package com.survey.backend.exceptions;

import com.stripe.exception.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
    AccessDeniedException.class,
    SecurityException.class,
    AuthenticationException.class
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
}
