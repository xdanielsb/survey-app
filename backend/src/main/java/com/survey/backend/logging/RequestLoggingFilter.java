package com.survey.backend.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Populates MDC logging context with request and user information so that all log statements
 * contain consistent identifiers for better monitoring.
 */
@Component
@Order(1) // Ensure this runs before other filters that might log
public class RequestLoggingFilter extends OncePerRequestFilter {
  private static final String HEADER_REQUEST_ID = "X-Request-ID";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String requestId = request.getHeader(HEADER_REQUEST_ID);
      if (requestId == null || requestId.isBlank()) {
        requestId = UUID.randomUUID().toString();
      }
      MDC.put("requestId", requestId);

      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
        MDC.put("userId", String.valueOf(auth.getPrincipal()));
      }

      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
