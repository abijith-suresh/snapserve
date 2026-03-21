package com.snapserve.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;

import com.snapserve.common.jwt.JwtUtils;
import com.snapserve.common.model.Role;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

class AuthenticationInterceptorTest {

  private JwtUtils jwtUtils;
  private AuthenticationInterceptor authenticationInterceptor;

  @BeforeEach
  void setUp() {
    jwtUtils = new JwtUtils();
    ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "01234567890123456789012345678901");
    jwtUtils.init();

    authenticationInterceptor = new AuthenticationInterceptor();
    ReflectionTestUtils.setField(authenticationInterceptor, "jwtUtils", jwtUtils);
    ReflectionTestUtils.setField(authenticationInterceptor, "routeValidator", new RouteValidator());
  }

  @Test
  void preHandleAllowsAuthServiceSpecialistTokenOnSpecialistRoute() throws Exception {
    MockHttpServletRequest request =
        new MockHttpServletRequest("GET", "/api/v1/specialists/profile");
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.addHeader(
        HttpHeaders.AUTHORIZATION,
        "Bearer " + createAuthServiceToken("specialist@snapserve.com", Role.SPECIALIST));

    boolean allowed = authenticationInterceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(request.getAttribute("X-User-Email")).isEqualTo("specialist@snapserve.com");
    assertThat(request.getAttribute("X-User-Roles")).isEqualTo(Role.SPECIALIST.name());
  }

  @Test
  void preHandleAllowsAuthServiceCustomerTokenOnCustomerRoute() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/customers/me");
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.addHeader(
        HttpHeaders.AUTHORIZATION,
        "Bearer " + createAuthServiceToken("customer@snapserve.com", Role.CUSTOMER));

    boolean allowed = authenticationInterceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(request.getAttribute("X-User-Email")).isEqualTo("customer@snapserve.com");
    assertThat(request.getAttribute("X-User-Roles")).isEqualTo(Role.CUSTOMER.name());
  }

  @Test
  void preHandleAllowsRefreshWithoutBearerToken() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/refresh");
    MockHttpServletResponse response = new MockHttpServletResponse();

    boolean allowed = authenticationInterceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  void preHandleAllowsLogoutWithoutBearerToken() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/logout");
    MockHttpServletResponse response = new MockHttpServletResponse();

    boolean allowed = authenticationInterceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  void preHandleRejectsRefreshSubPathWithoutBearerToken() throws Exception {
    MockHttpServletRequest request =
        new MockHttpServletRequest("POST", "/api/v1/auth/refresh/token");
    MockHttpServletResponse response = new MockHttpServletResponse();

    boolean allowed = authenticationInterceptor.preHandle(request, response, new Object());

    assertThat(allowed).isFalse();
    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getErrorMessage()).isEqualTo("Missing or invalid Authorization header");
  }

  @Test
  void preHandleRejectsLogoutSubPathWithoutBearerToken() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/logout/all");
    MockHttpServletResponse response = new MockHttpServletResponse();

    boolean allowed = authenticationInterceptor.preHandle(request, response, new Object());

    assertThat(allowed).isFalse();
    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getErrorMessage()).isEqualTo("Missing or invalid Authorization header");
  }

  private String createAuthServiceToken(String email, Role role) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(email)
        .issuedAt(new Date(now))
        .expiration(new Date(now + 3_600_000L))
        .claim("role", role.name())
        .signWith(jwtUtils.getSigningKey())
        .compact();
  }
}
