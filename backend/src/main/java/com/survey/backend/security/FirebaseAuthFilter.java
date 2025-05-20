package com.survey.backend.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    extractBearerToken(request)
        .flatMap(this::verifyToken)
        .ifPresent(
            token -> {
              var authentication =
                  new UsernamePasswordAuthenticationToken(
                      token.getUid(),
                      null,
                      Collections.emptyList() // or extract roles from token if using custom claims
                      );
              SecurityContextHolder.getContext().setAuthentication(authentication);
            });

    chain.doFilter(request, response);
  }

  private Optional<String> extractBearerToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return Optional.of(header.substring(7));
    }
    return Optional.empty();
  }

  private Optional<FirebaseToken> verifyToken(String token) {
    try {
      FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
      return Optional.of(decoded);
    } catch (FirebaseAuthException e) {
      System.out.println("Invalid Firebase token: {}" + e.getMessage()); // avoids log spam
      return Optional.empty();
    }
  }
}
