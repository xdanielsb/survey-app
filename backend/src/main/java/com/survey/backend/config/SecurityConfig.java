package com.survey.backend.config;

import com.survey.backend.security.FirebaseAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Configuration
@Component
@EnableMethodSecurity(prePostEnabled = true) // Enable globally @PreAuthorize and others
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, FirebaseAuthFilter firebaseAuthFilter)
      throws Exception {
    http.securityMatcher("/**") // optional, defaults to all
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/surveys", "/surveys/*", "/surveys/*/results", "/surveys/*/responses")
                    .permitAll()
                    .requestMatchers("/auth/**", "/users/**")
                    .permitAll()
                    .requestMatchers("/surveys/create")
                    .authenticated()
                    .anyRequest()
                    .denyAll())
        .addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }
}
