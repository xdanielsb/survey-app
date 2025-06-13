package com.survey.backend.config;

import com.survey.backend.security.KeycloakAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Configuration
@Component
@Profile("!shell")
@EnableMethodSecurity(prePostEnabled = true) // Enable globally @PreAuthorize and others
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, KeycloakAuthenticationFilter keycloakAuthenticationFilter)
      throws Exception {
    http.securityMatcher("/**")
        .authorizeHttpRequests(
            auth ->
                auth
                    // Public routes (auth and some surveys)
                    .requestMatchers(
                        "/auth/login",
                        "/users",
                        "/users/create",
                        "/payments/webhook",
                        "/payments/webhook/**",
                        "/surveys",
                        "/surveys/*/chat",
                        "/surveys/*",
                        "/surveys/*/responses",
                        "/surveys/*/results",
                        "/actuator/prometheus",
                        "/actuator/health",
                        "/actuator/info")
                    .permitAll()

                    // Authenticated-only routes
                    .requestMatchers(
                        "/surveys/create",
                        "/surveys/delete/**",
                        "/payments/verify",
                        "/payments/verify/**",
                        "/payments/session",
                        "/payments/verify",
                        "/users/credits",
                        "/payments")
                    .authenticated()
                    // Everything else is denied
                    .anyRequest()
                    .denyAll())
        .anonymous(AbstractHttpConfigurer::disable)
        .addFilterBefore(keycloakAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                        (req, res, ex2) ->
                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED"))
                    .accessDeniedHandler(
                        (req, res, ex2) ->
                            res.sendError(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN")))
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }
}
