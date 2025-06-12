package com.survey.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class KeycloakAuthenticationFilterTest {

  @Mock private RestTemplate restTemplate;
  @Mock private com.survey.backend.service.KeycloakAdminService keycloakAdminService;

  @InjectMocks private KeycloakAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(filter, "keycloakUrl", "http://localhost");
    ReflectionTestUtils.setField(filter, "keycloakRealm", "test");
  }

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void skipsAuthenticationWhenNoTokenPresent() throws ServletException, IOException {
    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/surveys");
    MockHttpServletResponse res = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(req, res, chain);

    assertEquals(200, res.getStatus());
    assertNotNull(chain.getRequest());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void authenticatesUsingBearerHeader() throws Exception {
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(Map.of("email", "user@example.com"), HttpStatus.OK));
    when(keycloakAdminService.getUserRoles("user@example.com")).thenReturn(List.of("ADMIN"));

    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/surveys");
    req.addHeader(HttpHeaders.AUTHORIZATION, "Bearer 123");
    MockHttpServletResponse res = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(req, res, chain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertEquals(200, res.getStatus());
    assertNotNull(chain.getRequest());
    assertNotNull(auth);
    assertEquals("user@example.com", auth.getPrincipal());
    assertEquals(1, auth.getAuthorities().size());
  }

  @Test
  void authenticatesUsingCookieToken() throws Exception {
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(Map.of("email", "cookie@example.com"), HttpStatus.OK));
    when(keycloakAdminService.getUserRoles("cookie@example.com")).thenReturn(List.of());

    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/surveys");
    req.setCookies(new Cookie("token", "abc"));
    MockHttpServletResponse res = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(req, res, chain);

    assertEquals(200, res.getStatus());
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void returnsUnauthorizedWhenUserInfoRequestFails() throws Exception {
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/surveys");
    req.addHeader(HttpHeaders.AUTHORIZATION, "Bearer bad");
    MockHttpServletResponse res = new MockHttpServletResponse();

    filter.doFilter(req, res, new MockFilterChain());

    assertEquals(401, res.getStatus());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void returnsUnauthorizedOnExchangeException() throws Exception {
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
        .thenThrow(new RuntimeException("boom"));

    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/surveys");
    req.addHeader(HttpHeaders.AUTHORIZATION, "Bearer 999");
    MockHttpServletResponse res = new MockHttpServletResponse();

    filter.doFilter(req, res, new MockFilterChain());

    assertEquals(401, res.getStatus());
  }

  @Test
  void returnsUnauthorizedWhenEmailMissing() throws Exception {
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(Map.of("name", "missing"), HttpStatus.OK));

    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/surveys");
    req.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token");
    MockHttpServletResponse res = new MockHttpServletResponse();

    filter.doFilter(req, res, new MockFilterChain());

    assertEquals(401, res.getStatus());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void stillAuthenticatesWhenRoleLookupFails() throws Exception {
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(Map.of("email", "rolefail@example.com"), HttpStatus.OK));
    when(keycloakAdminService.getUserRoles("rolefail@example.com"))
        .thenThrow(new RuntimeException("roles"));

    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/surveys");
    req.addHeader(HttpHeaders.AUTHORIZATION, "Bearer t1");
    MockHttpServletResponse res = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(req, res, chain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertEquals(200, res.getStatus());
    assertNotNull(chain.getRequest());
    assertNotNull(auth);
    assertTrue(auth.getAuthorities().isEmpty());
  }
}
