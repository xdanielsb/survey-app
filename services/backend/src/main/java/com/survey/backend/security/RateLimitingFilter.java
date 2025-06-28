package com.survey.backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Simple IP-based rate limiting filter using Bucket4j, with support for X-Forwarded-For headers.
 */
@Component
@Order(3)
public class RateLimitingFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

  private final int capacity;
  private final Duration refillDuration;
  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
  private final Supplier<Bucket> bucketSupplier;

  public RateLimitingFilter() {
    this(100, Duration.ofMillis(60000));
  }

  public RateLimitingFilter(
      @Value("${ratelimit.capacity:100}") int capacity,
      @Value("${ratelimit.refill-ms:60000}") long refillMs) {
    this(capacity, Duration.ofMillis(refillMs));
  }

  // Constructor for testability
  RateLimitingFilter(int capacity, Duration refillDuration) {
    this.capacity = capacity;
    this.refillDuration = refillDuration;
    this.bucketSupplier = this::createBucket;
  }

  private Bucket createBucket() {
    Refill refill = Refill.intervally(capacity, refillDuration);
    Bandwidth limit = Bandwidth.classic(capacity, refill);
    return Bucket.builder().addLimit(limit).build();
  }

  private Bucket resolveBucket(String key) {
    return buckets.computeIfAbsent(key, k -> bucketSupplier.get());
  }

  /** Resolves client IP address, prioritizing X-Forwarded-For header. */
  private String resolveClientIp(HttpServletRequest request) {
    String forwardedFor = request.getHeader("X-Forwarded-For");
    if (forwardedFor != null && !forwardedFor.isBlank()) {
      String[] ips = forwardedFor.split(",");
      for (String ip : ips) {
        String trimmed = ip.trim();
        if (!trimmed.isEmpty()) {
          return trimmed;
        }
      }
    }
    return request.getRemoteAddr();
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path != null && path.startsWith("/actuator");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String clientIp = resolveClientIp(request);
    String fullIpChain = request.getHeader("X-Forwarded-For");
    String userAgent = request.getHeader("User-Agent");
    String host = request.getHeader("Host");
    String method = request.getMethod();
    String uri = request.getRequestURI();
    String query = request.getQueryString();

    Bucket bucket = resolveBucket(clientIp);

    if (bucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      log.warn(
          """
            Rate limit exceeded:
            - Client IP: {}
            - Full IP Chain: {}
            - Method: {}
            - URI: {}{}
            - Host: {}
            - User-Agent: {}
            """,
          clientIp,
          fullIpChain != null ? fullIpChain : "(none)",
          method,
          uri,
          query != null ? "?" + query : "",
          host != null ? host : "(unknown)",
          userAgent != null ? userAgent : "(unknown)");
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.getWriter().write("Rate limit exceeded");
    }
  }
}
