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
  void blocksRequestsWhenLimitExceeded() throws ServletException, IOException {
    RateLimitingFilter filter = new RateLimitingFilter(1, Duration.ofMinutes(1));

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.setRequestURI("/auth/login");
    req.setRemoteAddr("1.2.3.4");

    MockHttpServletResponse res1 = new MockHttpServletResponse();
    filter.doFilter(req, res1, new MockFilterChain());
    assertEquals(200, res1.getStatus());

    MockHttpServletResponse res2 = new MockHttpServletResponse();
    filter.doFilter(req, res2, new MockFilterChain());
    assertEquals(429, res2.getStatus());
  }
}
