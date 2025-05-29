package com.survey.backend.exceptions;

import com.stripe.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<String> handleSecurity(SecurityException ex) {
    return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body(ex.getMessage());
  }

  @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
  public ResponseEntity<ApiError> onAuth(Throwable ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ApiError("AUTH_ERROR", ex.getMessage()));
  }
}
