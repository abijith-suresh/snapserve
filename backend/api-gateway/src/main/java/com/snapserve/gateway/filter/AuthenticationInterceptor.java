package com.snapserve.gateway.filter;

import com.snapserve.common.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

  static final String USER_EMAIL_ATTRIBUTE = "X-User-Email";
  static final String USER_ROLES_ATTRIBUTE = "X-User-Roles";

  @Autowired private JwtUtils jwtUtils;
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
    if (!jwtUtils.isValid(token)) {
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
      return false;
    }

    Claims claims = jwtUtils.extractClaims(token);
    String roles = extractRoles(claims);
    if (!hasRequiredRole(roles, path)) {
      response.sendError(HttpStatus.FORBIDDEN.value(), "Insufficient role for this resource");
      return false;
    }

    request.setAttribute(USER_EMAIL_ATTRIBUTE, claims.getSubject());
    request.setAttribute(USER_ROLES_ATTRIBUTE, roles);

    return true;
  }

  private String extractRoles(Claims claims) {
    String role = claims.get("role", String.class);
    if (role != null && !role.isBlank()) {
      return normalizeRoles(role);
    }

    return normalizeRoles(claims.get("roles", String.class));
  }

  private String normalizeRoles(String roles) {
    if (roles == null || roles.isBlank()) {
      return null;
    }

    return Arrays.stream(roles.split(","))
        .map(String::trim)
        .filter(role -> !role.isEmpty())
        .map(role -> role.toUpperCase(Locale.ROOT))
        .collect(Collectors.joining(","));
  }

  private boolean hasRequiredRole(String roles, String path) {
    if (roles == null) return false;
    List<String> roleList = Arrays.asList(roles.split(","));
    if (path.contains("/specialists")) return roleList.contains("SPECIALIST");
    if (path.contains("/customers")) return roleList.contains("CUSTOMER");
    return true;
  }
}
