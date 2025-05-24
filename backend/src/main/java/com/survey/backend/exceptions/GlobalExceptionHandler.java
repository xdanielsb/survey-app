package com.survey.backend.exceptions;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<String> handleSecurity(SecurityException ex) {
    return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body(ex.getMessage());
  }
}
