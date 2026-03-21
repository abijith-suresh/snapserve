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
import java.util.List;
import java.util.Locale;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Servlet filter that runs after {@link AuthenticationInterceptor} has validated the JWT and set
 * request attributes. It wraps the request to inject X-User-Email and X-User-Roles headers so
 * downstream services can trust the caller's identity without accepting spoofed incoming values.
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

    private static final String USER_EMAIL_HEADER = AuthenticationInterceptor.USER_EMAIL_ATTRIBUTE;
    private static final String USER_ROLES_HEADER = AuthenticationInterceptor.USER_ROLES_ATTRIBUTE;

    UserContextRequestWrapper(HttpServletRequest request) {
      super(request);
    }

    @Override
    public String getHeader(String name) {
      String injectedHeader = resolveInjectedHeader(name);
      return injectedHeader != null ? injectedHeader : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
      String injectedHeader = resolveInjectedHeader(name);
      if (injectedHeader != null) {
        return Collections.enumeration(Collections.singletonList(injectedHeader));
      }

      return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
      List<String> names = new ArrayList<>(Collections.list(super.getHeaderNames()));
      if (resolveInjectedHeader(USER_EMAIL_HEADER) != null && !names.contains(USER_EMAIL_HEADER)) {
        names.add(USER_EMAIL_HEADER);
      }
      if (resolveInjectedHeader(USER_ROLES_HEADER) != null && !names.contains(USER_ROLES_HEADER)) {
        names.add(USER_ROLES_HEADER);
      }

      return Collections.enumeration(names);
    }

    private String resolveInjectedHeader(String name) {
      String normalizedName = normalizeHeaderName(name);
      if (normalizeHeaderName(USER_EMAIL_HEADER).equals(normalizedName)) {
        Object email = getAttribute(USER_EMAIL_HEADER);
        return email != null ? email.toString() : null;
      }
      if (normalizeHeaderName(USER_ROLES_HEADER).equals(normalizedName)) {
        Object roles = getAttribute(USER_ROLES_HEADER);
        return roles != null ? roles.toString() : null;
      }
      return null;
    }

    private String normalizeHeaderName(String name) {
      return name == null ? null : name.toLowerCase(Locale.ROOT);
    }
  }
}
