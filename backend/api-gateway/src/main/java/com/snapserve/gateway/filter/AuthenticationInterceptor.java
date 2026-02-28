package com.snapserve.gateway.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

  @Autowired private JwtTokenValidator jwtValidator;
  @Autowired private RouteValidator routeValidator;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    String path = request.getRequestURI();
    if (!routeValidator.isSecured(path)) {
      return true;
    }

    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
      return false;
    }

    String token = authHeader.substring(7);
    if (!jwtValidator.isValid(token)) {
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
      return false;
    }

    Claims claims = jwtValidator.extractClaims(token);
    String roles = claims.get("roles", String.class);
    if (!hasRequiredRole(roles, path)) {
      response.sendError(HttpStatus.FORBIDDEN.value(), "Insufficient role for this resource");
      return false;
    }

    return true;
  }

  private boolean hasRequiredRole(String roles, String path) {
    if (roles == null) return false;
    if (path.contains("/specialists")) return roles.contains("specialist");
    if (path.contains("/customers")) return roles.contains("customer");
    return true;
  }
}
