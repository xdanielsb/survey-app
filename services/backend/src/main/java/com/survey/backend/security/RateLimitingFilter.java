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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Simple IP based rate limiting filter using Bucket4j. */
@Component
@Order(3)
public class RateLimitingFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

  @Value("${ratelimit.capacity:100}")
  private int capacity;

  @Value("${ratelimit.refill-ms:60000}")
  private Duration refillDuration;

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  public RateLimitingFilter() {
    this(100, Duration.ofMillis(60000));
  }

  public RateLimitingFilter(
      @Value("${ratelimit.capacity:100}") int capacity,
      @Value("${ratelimit.refill-ms:60000}") long refillMs) {
    this(capacity, Duration.ofMillis(refillMs));
  }

  // Constructor for tests
  RateLimitingFilter(int capacity, Duration refillDuration) {
    this.capacity = capacity;
    this.refillDuration = refillDuration;
  }

  private Bucket newBucket() {
    Refill refill = Refill.intervally(capacity, refillDuration);
    Bandwidth limit = Bandwidth.classic(capacity, refill);
    return Bucket.builder().addLimit(limit).build();
  }

  private Bucket resolveBucket(String key) {
    return buckets.computeIfAbsent(key, k -> newBucket());
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
    String ip = request.getRemoteAddr();
    Bucket bucket = resolveBucket(ip);
    if (bucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      log.warn("Rate limit exceeded for ip={}", ip);
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.getWriter().write("Rate limit exceeded");
    }
  }
}
