package com.survey.backend.logging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.*;

class HttpLoggingFilterTest {

  private HttpLoggingFilter filter;

  @BeforeEach
  void setUp() {
    filter = new HttpLoggingFilter();
  }

  @Test
  void shouldSkipLoggingForActuatorPath() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, chain);

    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  void shouldLogRequestWithBodyAndHeaders() throws Exception {
    MockHttpServletRequest rawRequest = new MockHttpServletRequest("POST", "/api/test");
    rawRequest.setContentType("application/json");
    rawRequest.setContent(
        "{\"username\":\"john\",\"password\":\"123456\"}".getBytes(StandardCharsets.UTF_8));
    rawRequest.addHeader("User-Agent", "JUnit");
    rawRequest.addHeader("Authorization", "Bearer token123");
    rawRequest.addHeader("X-Forwarded-For", "192.168.1.100");

    MockHttpServletResponse rawResponse = new MockHttpServletResponse();

    FilterChain chain =
        (req, res) -> {
          res.getWriter().write("{\"message\":\"ok\"}");
        };

    filter.doFilterInternal(rawRequest, rawResponse, chain);

    assertEquals(200, rawResponse.getStatus());
    assertTrue(rawResponse.getContentAsString().contains("ok"));
  }

  @Test
  void getClientIpAddressShouldReturnForwardedIpIfAvailable() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1, proxy");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");

    var method =
        HttpLoggingFilter.class.getDeclaredMethod("getClientIpAddress", HttpServletRequest.class);
    method.setAccessible(true); // ← This line is essential

    String ip = (String) method.invoke(filter, request);

    assertEquals("10.0.0.1", ip);
  }

  @Test
  void sanitizeBodyShouldRedactSensitiveKeys() throws Exception {
    String rawJson = "{\"username\":\"john\",\"password\":\"abc\",\"token\":\"xyz\"}";

    var sanitizeBodyMethod =
        HttpLoggingFilter.class.getDeclaredMethod("sanitizeBody", String.class);
    sanitizeBodyMethod.setAccessible(true);

    String sanitized = (String) sanitizeBodyMethod.invoke(filter, rawJson);

    assertFalse(sanitized.contains("abc"));
    assertFalse(sanitized.contains("xyz"));
    assertTrue(sanitized.contains("\"password\":\"[REDACTED]\""));
  }

  @Test
  void sanitizeHeadersShouldRedactSensitiveHeaders() throws Exception {
    Map<String, String> headers =
        Map.of(
            "Authorization", "Bearer secret",
            "Content-Type", "application/json");

    var sanitizeHeadersMethod =
        HttpLoggingFilter.class.getDeclaredMethod("sanitizeHeaders", Map.class);
    sanitizeHeadersMethod.setAccessible(true);

    Map<String, String> sanitized =
        (Map<String, String>) sanitizeHeadersMethod.invoke(filter, headers);

    assertEquals("[REDACTED]", sanitized.get("Authorization"));
    assertEquals("application/json", sanitized.get("Content-Type"));
  }
}
