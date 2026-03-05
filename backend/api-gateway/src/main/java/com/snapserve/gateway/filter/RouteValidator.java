package com.snapserve.gateway.filter;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

  public static final List<String> OPEN_ENDPOINTS =
      List.of("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/validate/token");

  public boolean isSecured(String path) {
    return OPEN_ENDPOINTS.stream().noneMatch(path::startsWith);
  }
}
