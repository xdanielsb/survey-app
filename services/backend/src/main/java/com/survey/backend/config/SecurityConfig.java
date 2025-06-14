package com.survey.backend.config;

import com.survey.backend.security.KeycloakAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Profile("!shell")
@EnableMethodSecurity(prePostEnabled = true) // Enables @PreAuthorize and similar
public class SecurityConfig {

  private static final String[] PUBLIC_ENDPOINTS = {
    "/auth/login",
    "/users",
    "/users/create",
    "/payments/webhook",
    "/payments/webhook/**",
    "/surveys",
    "/surveys/*",
    "/surveys/*/chat",
    "/surveys/*/responses",
    "/surveys/*/results",
    "/actuator/prometheus",
    "/actuator/health",
    "/actuator/info"
  };

  private static final String[] AUTHENTICATED_ENDPOINTS = {
    "/surveys/create",
    "/surveys/delete/**",
    "/payments/verify",
    "/payments/verify/**",
    "/payments/session",
    "/users/credits",
    "/payments"
  };

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, KeycloakAuthenticationFilter keycloakAuthenticationFilter)
      throws Exception {

    http.securityMatcher("/**")
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(PUBLIC_ENDPOINTS)
                    .permitAll()
                    .requestMatchers(AUTHENTICATED_ENDPOINTS)
                    .authenticated()
                    .anyRequest()
                    .denyAll())
        .anonymous(AbstractHttpConfigurer::disable)
        .addFilterBefore(keycloakAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(SecurityConfig::configureErrorHandling)
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

  private static void configureErrorHandling(ExceptionHandlingConfigurer<HttpSecurity> ex) {
    ex.authenticationEntryPoint(
            (req, res, e) ->
                res.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase()))
        .accessDeniedHandler(
            (req, res, e) ->
                res.sendError(
                    HttpServletResponse.SC_FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase()));
  }
}
