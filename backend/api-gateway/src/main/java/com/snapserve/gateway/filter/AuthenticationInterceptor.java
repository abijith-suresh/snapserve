package com.snapserve.gateway.filter;

import com.snapserve.common.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

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
    String roles = claims.get("roles", String.class);
    if (!hasRequiredRole(roles, path)) {
      response.sendError(HttpStatus.FORBIDDEN.value(), "Insufficient role for this resource");
      return false;
    }

    request.setAttribute("X-User-Email", claims.getSubject());
    request.setAttribute("X-User-Roles", roles);

    return true;
  }

  private boolean hasRequiredRole(String roles, String path) {
    if (roles == null) return false;
    List<String> roleList = Arrays.asList(roles.split(","));
    if (path.contains("/specialists")) return roleList.contains("specialist");
    if (path.contains("/customers")) return roleList.contains("customer");
    return true;
  }
}
