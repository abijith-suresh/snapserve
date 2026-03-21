package com.snapserve.gateway.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.snapserve.common.jwt.JwtUtils;
import com.snapserve.common.model.Role;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class GatewayAuthHeaderPropagationTest {

  private JwtUtils jwtUtils;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    jwtUtils = new JwtUtils();
    ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "01234567890123456789012345678901");
    jwtUtils.init();

    AuthenticationInterceptor authenticationInterceptor = new AuthenticationInterceptor();
    ReflectionTestUtils.setField(authenticationInterceptor, "jwtUtils", jwtUtils);
    ReflectionTestUtils.setField(authenticationInterceptor, "routeValidator", new RouteValidator());

    mockMvc =
        MockMvcBuilders.standaloneSetup(new HeaderEchoController())
            .addInterceptors(authenticationInterceptor)
            .addFilters(new UserContextFilter())
            .build();
  }

  @Test
  void validCustomerTokenPropagatesTrustedHeadersToSecuredController() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/customers/context")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + createAuthServiceToken("customer@snapserve.com", Role.CUSTOMER)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("customer@snapserve.com"))
        .andExpect(jsonPath("$.roles").value("CUSTOMER"));
  }

  @Test
  void spoofedUserContextHeadersAreOverriddenByTrustedTokenClaims() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/customers/context")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + createAuthServiceToken("customer@snapserve.com", Role.CUSTOMER))
                .header("X-User-Email", "spoofed@snapserve.com")
                .header("X-User-Roles", "SPECIALIST"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("customer@snapserve.com"))
        .andExpect(jsonPath("$.roles").value("CUSTOMER"));
  }

  @Test
  void lowercaseSpoofedUserContextHeadersAreAlsoOverriddenByTrustedTokenClaims() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/customers/context")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + createAuthServiceToken("customer@snapserve.com", Role.CUSTOMER))
                .header("x-user-email", "spoofed@snapserve.com")
                .header("x-user-roles", "SPECIALIST"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("customer@snapserve.com"))
        .andExpect(jsonPath("$.roles").value("CUSTOMER"));
  }

  @Test
  void lowercaseHeaderLookupReceivesTrustedInjectedValues() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/customers/context-lowercase")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + createAuthServiceToken("customer@snapserve.com", Role.CUSTOMER))
                .header("x-user-email", "spoofed@snapserve.com")
                .header("x-user-roles", "SPECIALIST"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("customer@snapserve.com"))
        .andExpect(jsonPath("$.roles").value("CUSTOMER"));
  }

  @Test
  void customerTokenCannotAccessSpecialistOnlyRoute() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/specialists/context")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + createAuthServiceToken("customer@snapserve.com", Role.CUSTOMER)))
        .andExpect(status().isForbidden());
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

  @RestController
  static class HeaderEchoController {

    @GetMapping("/api/v1/customers/context")
    ResponseEntity<HeaderEchoResponse> customerContext(
        jakarta.servlet.http.HttpServletRequest request) {
      return ResponseEntity.ok(
          new HeaderEchoResponse(
              request.getHeader("X-User-Email"), request.getHeader("X-User-Roles")));
    }

    @GetMapping("/api/v1/specialists/context")
    ResponseEntity<HeaderEchoResponse> specialistContext(
        jakarta.servlet.http.HttpServletRequest request) {
      return ResponseEntity.ok(
          new HeaderEchoResponse(
              request.getHeader("X-User-Email"), request.getHeader("X-User-Roles")));
    }

    @GetMapping("/api/v1/customers/context-lowercase")
    ResponseEntity<HeaderEchoResponse> customerContextLowercase(
        jakarta.servlet.http.HttpServletRequest request) {
      return ResponseEntity.ok(
          new HeaderEchoResponse(
              request.getHeader("x-user-email"), request.getHeader("x-user-roles")));
    }
  }

  record HeaderEchoResponse(String email, String roles) {}
}
