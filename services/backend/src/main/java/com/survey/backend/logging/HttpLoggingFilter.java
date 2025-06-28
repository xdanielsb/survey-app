package com.survey.backend.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(0)
public class HttpLoggingFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

  private static final int MAX_BODY_LENGTH = 2000;
  private static final List<String> SENSITIVE_KEYS = List.of("password", "token", "secret");
  private static final Set<String> SENSITIVE_HEADERS = Set.of("authorization");

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (shouldSkipLogging(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
    ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

    long startTime = System.currentTimeMillis();
    filterChain.doFilter(wrappedRequest, wrappedResponse);
    long duration = System.currentTimeMillis() - startTime;

    logRequest(wrappedRequest, wrappedResponse, duration);
    wrappedResponse.copyBodyToResponse(); // Ensure response body is propagated
  }

  private boolean shouldSkipLogging(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path != null && path.startsWith("/actuator");
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String[] headerNames = {
      "X-Forwarded-For",
      "X-Real-IP",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_CLIENT_IP",
      "HTTP_X_FORWARDED_FOR"
    };

    for (String header : headerNames) {
      String ip = request.getHeader(header);
      if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
        return ip.split(",")[0].trim(); // In case of multiple IPs
      }
    }

    return request.getRemoteAddr();
  }

  private void logRequest(
      ContentCachingRequestWrapper request, HttpServletResponse response, long duration) {
    Map<String, Object> logDetails = new LinkedHashMap<>();
    logDetails.put("method", request.getMethod());
    logDetails.put("path", request.getRequestURI());
    logDetails.put("query", request.getQueryString());
    logDetails.put("status", response.getStatus());
    logDetails.put("duration_ms", duration);
    logDetails.put("remote_ip", this.getClientIpAddress(request));
    logDetails.put("user_agent", request.getHeader("User-Agent"));
    logDetails.put("headers", sanitizeHeaders(extractHeaders(request)));

    String requestBody = getRequestBody(request);
    if (!requestBody.isBlank()) {
      logDetails.put("body", sanitizeBody(requestBody));
    }

    log.info("HTTP Request Log: {}", logDetails);
  }

  private Map<String, String> extractHeaders(HttpServletRequest request) {
    Map<String, String> headers = new LinkedHashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();

    while (headerNames.hasMoreElements()) {
      String name = headerNames.nextElement();
      headers.put(name, request.getHeader(name));
    }

    return headers;
  }

  private Map<String, String> sanitizeHeaders(Map<String, String> headers) {
    return headers.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry ->
                    SENSITIVE_HEADERS.contains(entry.getKey().toLowerCase())
                        ? "[REDACTED]"
                        : entry.getValue(),
                (v1, v2) -> v1,
                LinkedHashMap::new));
  }

  private String sanitizeBody(String body) {
    String sanitized = body;
    for (String key : SENSITIVE_KEYS) {
      sanitized =
          sanitized.replaceAll(
              "(?i)\"" + key + "\"\\s*:\\s*\".*?\"", "\"" + key + "\":\"[REDACTED]\"");
    }
    return sanitized;
  }

  private String getRequestBody(ContentCachingRequestWrapper request) {
    byte[] content = request.getContentAsByteArray();
    if (content.length == 0) return "";

    String body = new String(content, StandardCharsets.UTF_8);
    return body.length() > MAX_BODY_LENGTH
        ? body.substring(0, MAX_BODY_LENGTH) + "...(truncated)"
        : body;
  }
}
