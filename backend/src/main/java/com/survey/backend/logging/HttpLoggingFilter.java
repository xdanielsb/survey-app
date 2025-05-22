package com.survey.backend.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);
  private static final int MAX_BODY_LOG_LENGTH = 2000;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
    ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

    long startTime = System.currentTimeMillis();
    filterChain.doFilter(wrappedRequest, wrappedResponse);
    long duration = System.currentTimeMillis() - startTime;

    Map<String, Object> logMap = new LinkedHashMap<>();
    logMap.put("method", request.getMethod());
    logMap.put("path", request.getRequestURI());
    logMap.put("query", request.getQueryString());
    logMap.put("status", response.getStatus());
    logMap.put("duration_ms", duration);
    logMap.put("remote_ip", request.getRemoteAddr());
    logMap.put("user_agent", request.getHeader("User-Agent"));

    // Extract headers
    Map<String, String> headers = new LinkedHashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String header = headerNames.nextElement();
      headers.put(header, request.getHeader(header));
    }
    logMap.put("headers", headers);

    // Request body
    String requestBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
    if (!requestBody.isBlank()) {
      logMap.put(
          "body",
          requestBody.length() > MAX_BODY_LOG_LENGTH
              ? requestBody.substring(0, MAX_BODY_LOG_LENGTH) + "...(truncated)"
              : requestBody);
    }

    log.info("HTTP Request Log: {}", logMap);

    wrappedResponse.copyBodyToResponse(); // Important to commit response
  }
}
