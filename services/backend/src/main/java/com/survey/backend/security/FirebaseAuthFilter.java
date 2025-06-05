package com.survey.backend.security;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.survey.backend.entity.Role;
import com.survey.backend.entity.User;
import com.survey.backend.exception.ExpiredTokenException;
import com.survey.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(FirebaseAuthFilter.class);
  private final UserRepository userRepository;

  public FirebaseAuthFilter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    return path != null && path.startsWith("/actuator");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    Optional<String> tokenOpt = extractBearerToken(request);
    Optional<FirebaseToken> decoded = tokenOpt.flatMap(this::verifyToken);

    if (decoded.isPresent()) {
      FirebaseToken token = decoded.get();

      // Load user + roles
      List<GrantedAuthority> authorities =
          userRepository
              .findByUid(token.getUid())
              .map(User::getRoles)
              .orElse(Set.of()) // fallback to empty set if user not found
              .stream()
              .map(Role::getName)
              .map(SimpleGrantedAuthority::new)
              .collect(Collectors.toList());

      // Set auth context
      var auth = new UsernamePasswordAuthenticationToken(token.getUid(), null, authorities);
      SecurityContextHolder.getContext().setAuthentication(auth);
      log.info(
          "Authenticated user email={} uid={} roles={}",
          token.getEmail(),
          token.getUid(),
          authorities.stream().map(GrantedAuthority::getAuthority).toList());
    }

    chain.doFilter(request, response);
  }

  private Optional<String> extractBearerToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return Optional.of(header.substring(7));
    }
    if (request.getCookies() != null) {
      String names =
          Arrays.stream(request.getCookies()).map(Cookie::getName).collect(Collectors.joining(","));
      log.info("cookies={}", names);
      for (Cookie cookie : request.getCookies()) {
        if ("token".equals(cookie.getName()) && !cookie.getValue().isBlank()) {
          return Optional.of(cookie.getValue());
        }
      }
    } else {
      log.info("No cookies found in request");
    }

    log.info(
        "Auth bypassed: No Bearer token found | method={} path={} ip={} user-agent={}",
        request.getMethod(),
        request.getRequestURI(),
        request.getRemoteAddr(),
        request.getHeader("User-Agent"));
    return Optional.empty();
  }

  private Optional<FirebaseToken> verifyToken(String token) {
    try {
      FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
      return Optional.of(decoded);
    } catch (FirebaseAuthException e) {
      if (e.getAuthErrorCode() == AuthErrorCode.EXPIRED_ID_TOKEN) {
        throw new ExpiredTokenException("TOKEN_EXPIRED");
      }
      log.warn("Invalid Firebase token: {}", e.getMessage());
      return Optional.empty();
    }
  }
}
