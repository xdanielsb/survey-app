package com.survey.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(2)
@RequiredArgsConstructor
public class KeycloakAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(KeycloakAuthenticationFilter.class);

  private final RestTemplate restTemplate;
  private final com.survey.backend.service.KeycloakAdminService keycloakAdminService;

  @Value("${keycloak.url}")
  private String keycloakUrl;

  @Value("${keycloak.realm}")
  private String keycloakRealm;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    return path != null && path.startsWith("/actuator");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    Optional<String> tokenOpt = extractBearerToken(request);

    if (tokenOpt.isEmpty()) {
      log.info("No Bearer token found. Skipping authentication.");
      filterChain.doFilter(request, response);
      return;
    }

    String token = tokenOpt.get();
    String userInfoUrl =
        String.format("%s/realms/%s/protocol/openid-connect/userinfo", keycloakUrl, keycloakRealm);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Map> resp;
    try {
      resp = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);
    } catch (Exception ex) {
      log.error("Token verification failed: {}", ex.getMessage());
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
      return;
    }

    if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
      log.warn("User info endpoint returned non-success status or empty body");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
      return;
    }

    Map<String, Object> body = resp.getBody();
    String email = (String) body.get("email");

    if (!StringUtils.hasText(email)) {
      log.warn("Missing email in userinfo response");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
      return;
    }

    List<String> roles = Collections.emptyList();
    try {
      roles = keycloakAdminService.getUserRoles(email != null ? email : "");
    } catch (Exception ex) {
      log.error("Failed to retrieve user roles for email={} : {}", email, ex.getMessage());
    }

    Authentication auth =
        new UsernamePasswordAuthenticationToken(
            email,
            null,
            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

    SecurityContextHolder.getContext().setAuthentication(auth);
    log.info("Authenticated user: {} with roles: {}", email, roles);

    filterChain.doFilter(request, response);
  }

  private Optional<String> extractBearerToken(HttpServletRequest request) {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header != null && header.startsWith("Bearer ")) {
      return Optional.of(header.substring(7));
    }

    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("token".equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
          return Optional.of(cookie.getValue());
        }
      }
    }

    log.info(
        "Auth bypassed: No Bearer token found | method={} path={} ip={} user-agent={}",
        request.getMethod(),
        request.getRequestURI(),
        request.getRemoteAddr(),
        request.getHeader("User-Agent"));

    return Optional.empty();
  }
}
