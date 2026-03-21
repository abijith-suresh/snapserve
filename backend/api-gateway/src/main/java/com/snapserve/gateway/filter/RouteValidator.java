package com.snapserve.gateway.filter;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

  public static final List<String> OPEN_ENDPOINTS =
      List.of(
          "/api/v1/auth/register",
          "/api/v1/auth/login",
          "/api/v1/auth/refresh",
          "/api/v1/auth/logout",
          "/api/v1/auth/validate/token",
          "/actuator");

  private static final List<String> OPEN_ENDPOINT_PREFIXES = List.of("/actuator/");

  public boolean isSecured(String path) {
    String normalizedPath = normalizePath(path);
    if (normalizedPath == null || normalizedPath.isBlank()) {
      return true;
    }

    return OPEN_ENDPOINTS.stream().noneMatch(openEndpoint -> openEndpoint.equals(normalizedPath))
        && OPEN_ENDPOINT_PREFIXES.stream().noneMatch(normalizedPath::startsWith);
  }

  private String normalizePath(String path) {
    if (path == null || path.isBlank()) {
      return path;
    }

    if (path.length() > 1 && path.endsWith("/")) {
      return path.substring(0, path.length() - 1);
    }

    return path;
  }
}
