package com.survey.backend.config;

import com.survey.backend.security.FirebaseAuthFilter;
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
  public SecurityFilterChain filterChain(HttpSecurity http, FirebaseAuthFilter firebaseAuthFilter)
      throws Exception {
    http.securityMatcher("/**")
        .authorizeHttpRequests(
            auth ->
                auth
                    // Public routes (auth and some surveys)
                    .requestMatchers(
                        "/auth/login",
                        "/users/**",
                        "/payments/session",
                        "/payments/webhook",
                        "/payments/verify",
                        "/payments/webhook/**",
                        "/payments/verify/**",
                        "/surveys",
                        "/surveys/*",
                        "/surveys/*/results",
                        "/surveys/*/responses")
                    .permitAll()

                    // Authenticated-only routes
                    .requestMatchers("/surveys/create", "/surveys/delete/**", "/users/credits")
                    .authenticated()

                    // Everything else is permitted
                    .anyRequest()
                    .denyAll())

        // Add Firebase filter before username-password filter
        .addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class)

        // Disable CSRF for API use (enable for forms)
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }
}
