package com.survey.backend.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

class WebConfigTest {

  private static final String TEST_ALLOWED_ORIGIN = "http://example.com";

  @Test
  void corsConfigurer_shouldSetExpectedCorsProperties() {
    // Arrange
    WebConfig config = new WebConfig();
    setAllowedOriginsViaReflection(config, TEST_ALLOWED_ORIGIN);
    WebMvcConfigurer configurer = config.corsConfigurer();
    MockCorsRegistry registry = new MockCorsRegistry();

    // Act
    configurer.addCorsMappings(registry);

    // Assert
    assertThat(registry.mapping).isEqualTo("/**");
    assertThat(registry.allowedOrigins)
        .as("Allowed origins should match")
        .containsExactly(TEST_ALLOWED_ORIGIN);
    assertThat(registry.allowedMethods)
        .as("Allowed methods should cover standard HTTP verbs")
        .containsExactlyInAnyOrder("GET", "POST", "PUT", "DELETE", "OPTIONS");
    assertThat(registry.allowedHeaders).as("All headers should be allowed").containsExactly("*");
  }

  private void setAllowedOriginsViaReflection(WebConfig config, String origin) {
    try {
      var field = WebConfig.class.getDeclaredField("allowedOrigins");
      field.setAccessible(true);
      field.set(config, origin);
    } catch (Exception e) {
      throw new RuntimeException("Failed to inject allowedOrigins via reflection", e);
    }
  }

  static class MockCorsRegistry
      extends org.springframework.web.servlet.config.annotation.CorsRegistry {
    String mapping;
    String[] allowedOrigins;
    String[] allowedMethods;
    String[] allowedHeaders;

    @Override
    public CorsRegistration addMapping(String pathPattern) {
      this.mapping = pathPattern;
      return new CorsRegistration(pathPattern) {
        @Override
        public CorsRegistration allowedOrigins(String... origins) {
          MockCorsRegistry.this.allowedOrigins = origins;
          return this;
        }

        @Override
        public CorsRegistration allowedMethods(String... methods) {
          MockCorsRegistry.this.allowedMethods = methods;
          return this;
        }

        @Override
        public CorsRegistration allowedHeaders(String... headers) {
          MockCorsRegistry.this.allowedHeaders = headers;
          return this;
        }
      };
    }
  }
}
