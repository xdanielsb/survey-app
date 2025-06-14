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
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String TOKEN_COOKIE_NAME = "token";

  private final RestTemplate restTemplate;
  private final com.survey.backend.service.KeycloakAdminService keycloakAdminService;

  @Value("${keycloak.url}")
  private String keycloakUrl;

  @Value("${keycloak.realm}")
  private String keycloakRealm;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path != null && path.startsWith("/actuator");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    extractBearerToken(request)
        .ifPresentOrElse(
            token -> authenticateWithToken(token, request, response, filterChain),
            () -> {
              log.info("No Bearer token found. Skipping authentication.");
              try {
                filterChain.doFilter(request, response);
              } catch (IOException | ServletException e) {
                log.error("Error during filter chain execution", e);
              }
            });
  }

  private void authenticateWithToken(
      String token,
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) {
    String userInfoUrl =
        String.format("%s/realms/%s/protocol/openid-connect/userinfo", keycloakUrl, keycloakRealm);
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    try {
      ResponseEntity<Map> resp =
          restTemplate.exchange(userInfoUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

      if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
        log.warn("User info endpoint failed or returned empty response");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
        return;
      }

      processAuthenticationResponse(resp.getBody(), response, filterChain, request);

    } catch (Exception ex) {
      log.error(
          "Error during token verification: {} - {}", ex.getClass().getSimpleName(), ex.toString());
      log.error("Token verification failed: {}", ex.getMessage());
      try {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
      } catch (IOException e) {
        log.error("Failed to send unauthorized response", e);
      }
    }
  }

  private void processAuthenticationResponse(
      Map<String, Object> userInfo,
      HttpServletResponse response,
      FilterChain filterChain,
      HttpServletRequest request)
      throws IOException, ServletException {

    String email = (String) userInfo.get("email");

    if (!StringUtils.hasText(email)) {
      log.warn("Missing email in userinfo response");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
      return;
    }

    List<String> roles;
    try {
      roles = keycloakAdminService.getUserRoles(email);
    } catch (Exception ex) {
      log.error("Failed to retrieve roles for user {}: {}", email, ex.getMessage());
      roles = Collections.emptyList();
    }

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            email,
            null,
            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.info("Authenticated user: {} with roles: {}", email, roles);

    filterChain.doFilter(request, response);
  }

  private Optional<String> extractBearerToken(HttpServletRequest request) {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
      return Optional.of(header.substring(BEARER_PREFIX.length()));
    }

    if (request.getCookies() != null) {
      return Arrays.stream(request.getCookies())
          .filter(
              cookie ->
                  TOKEN_COOKIE_NAME.equals(cookie.getName())
                      && StringUtils.hasText(cookie.getValue()))
          .map(Cookie::getValue)
          .findFirst();
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
