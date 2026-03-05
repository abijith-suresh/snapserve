package com.snapserve.gateway.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Servlet filter that runs after {@link AuthenticationInterceptor} has validated the JWT and set
 * request attributes. It wraps the request to inject X-User-Email and X-User-Roles headers so
 * downstream services can trust the caller's identity without re-parsing the token.
 */
@Component
@Order(1)
public class UserContextFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    chain.doFilter(new UserContextRequestWrapper(httpRequest), response);
  }

  private static class UserContextRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> extraHeaders = new HashMap<>();

    UserContextRequestWrapper(HttpServletRequest request) {
      super(request);
      Object email = request.getAttribute("X-User-Email");
      Object roles = request.getAttribute("X-User-Roles");
      if (email != null) extraHeaders.put("X-User-Email", email.toString());
      if (roles != null) extraHeaders.put("X-User-Roles", roles.toString());
    }

    @Override
    public String getHeader(String name) {
      String extra = extraHeaders.get(name);
      return extra != null ? extra : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
      String extra = extraHeaders.get(name);
      if (extra != null) return Collections.enumeration(Collections.singletonList(extra));
      return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
      List<String> names = new ArrayList<>(Collections.list(super.getHeaderNames()));
      names.addAll(extraHeaders.keySet());
      return Collections.enumeration(names);
    }
  }
}
