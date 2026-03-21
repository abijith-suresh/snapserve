package com.snapserve.gateway.filter;

import com.snapserve.gateway.config.GatewayRateLimitProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class GatewayRateLimitFilter extends OncePerRequestFilter {

  private static final int CLEANUP_FREQUENCY = 1_000;

  private final GatewayRateLimitProperties properties;
  private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();
  private final AtomicLong requestCount = new AtomicLong();
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  public GatewayRateLimitFilter(GatewayRateLimitProperties properties) {
    this.properties = properties;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    if (!properties.isEnabled()) {
      return true;
    }

    String path = request.getRequestURI();
    boolean included =
        properties.getIncludePaths().stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    boolean excluded =
        properties.getExcludePaths().stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    return !included || excluded;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Instant now = Instant.now();
    long windowMillis = Duration.ofSeconds(properties.getWindowSeconds()).toMillis();
    String clientKey = resolveClientKey(request);

    WindowCounter currentWindow =
        counters.compute(
            clientKey,
            (key, existing) -> {
              if (existing == null || existing.expiresAtEpochMilli() <= now.toEpochMilli()) {
                return new WindowCounter(1, now.plusMillis(windowMillis).toEpochMilli());
              }

              return new WindowCounter(existing.requests() + 1, existing.expiresAtEpochMilli());
            });

    maybeCleanupExpiredCounters(now);

    if (currentWindow.requests() > properties.getRequestsPerWindow()) {
      long retryAfterSeconds =
          Math.max(
              1L,
              Duration.ofMillis(currentWindow.expiresAtEpochMilli() - now.toEpochMilli())
                  .toSeconds());
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setContentType("application/json");
      response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
      response.getWriter().write("{\"message\":\"Rate limit exceeded\"}");
      return;
    }

    response.setHeader("X-RateLimit-Limit", String.valueOf(properties.getRequestsPerWindow()));
    response.setHeader(
        "X-RateLimit-Remaining",
        String.valueOf(Math.max(0, properties.getRequestsPerWindow() - currentWindow.requests())));
    filterChain.doFilter(request, response);
  }

  private String resolveClientKey(HttpServletRequest request) {
    if (!properties.isUseForwardedFor()) {
      return request.getRemoteAddr();
    }

    String forwardedFor = request.getHeader("X-Forwarded-For");
    if (forwardedFor != null && !forwardedFor.isBlank()) {
      return forwardedFor.split(",")[0].trim();
    }

    return request.getRemoteAddr();
  }

  private void maybeCleanupExpiredCounters(Instant now) {
    if (requestCount.incrementAndGet() % CLEANUP_FREQUENCY != 0) {
      return;
    }

    counters
        .entrySet()
        .removeIf(entry -> entry.getValue().expiresAtEpochMilli() <= now.toEpochMilli());
  }

  record WindowCounter(int requests, long expiresAtEpochMilli) {}
}
