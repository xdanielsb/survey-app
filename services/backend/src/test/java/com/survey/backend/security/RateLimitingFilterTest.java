package com.survey.backend.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class RateLimitingFilterTest {

  @Test
  void blocksRequestsWhenLimitExceededWithRemoteAddr() throws ServletException, IOException {
    RateLimitingFilter filter = new RateLimitingFilter(1, Duration.ofMinutes(1));

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.setRequestURI("/api/test");
    req.setRemoteAddr("1.2.3.4");

    MockHttpServletResponse res1 = new MockHttpServletResponse();
    filter.doFilter(req, res1, new MockFilterChain());
    assertEquals(200, res1.getStatus());

    MockHttpServletResponse res2 = new MockHttpServletResponse();
    filter.doFilter(req, res2, new MockFilterChain());
    assertEquals(429, res2.getStatus());
  }

  @Test
  void blocksRequestsWhenLimitExceededWithXForwardedFor() throws ServletException, IOException {
    RateLimitingFilter filter = new RateLimitingFilter(1, Duration.ofMinutes(1));

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.setRequestURI("/api/test");
    req.addHeader("X-Forwarded-For", "9.8.7.6");

    MockHttpServletResponse res1 = new MockHttpServletResponse();
    filter.doFilter(req, res1, new MockFilterChain());
    assertEquals(200, res1.getStatus());

    MockHttpServletResponse res2 = new MockHttpServletResponse();
    filter.doFilter(req, res2, new MockFilterChain());
    assertEquals(429, res2.getStatus());
  }

  @Test
  void allowsDifferentIpsIndependently() throws ServletException, IOException {
    RateLimitingFilter filter = new RateLimitingFilter(1, Duration.ofMinutes(1));

    MockHttpServletRequest req1 = new MockHttpServletRequest();
    req1.setRequestURI("/api/test");
    req1.setRemoteAddr("1.1.1.1");

    MockHttpServletRequest req2 = new MockHttpServletRequest();
    req2.setRequestURI("/api/test");
    req2.setRemoteAddr("2.2.2.2");

    MockHttpServletResponse res1 = new MockHttpServletResponse();
    filter.doFilter(req1, res1, new MockFilterChain());
    assertEquals(200, res1.getStatus());

    MockHttpServletResponse res2 = new MockHttpServletResponse();
    filter.doFilter(req2, res2, new MockFilterChain());
    assertEquals(200, res2.getStatus());
  }

  @Test
  void usesFirstIpFromXForwardedForList() throws ServletException, IOException {
    RateLimitingFilter filter = new RateLimitingFilter(1, Duration.ofMinutes(1));

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.setRequestURI("/api/test");
    req.addHeader("X-Forwarded-For", "10.0.0.1, 192.168.1.1");
    req.setRemoteAddr("127.0.0.1"); // Should be ignored

    MockHttpServletResponse res1 = new MockHttpServletResponse();
    filter.doFilter(req, res1, new MockFilterChain());
    assertEquals(200, res1.getStatus());

    MockHttpServletResponse res2 = new MockHttpServletResponse();
    filter.doFilter(req, res2, new MockFilterChain());
    assertEquals(429, res2.getStatus());
  }
}
