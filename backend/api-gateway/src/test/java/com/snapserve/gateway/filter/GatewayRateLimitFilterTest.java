package com.snapserve.gateway.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.snapserve.gateway.config.GatewayRateLimitProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class GatewayRateLimitFilterTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    GatewayRateLimitProperties properties = new GatewayRateLimitProperties();
    properties.setEnabled(true);
    properties.setRequestsPerWindow(2);
    properties.setWindowSeconds(60);

    mockMvc =
        MockMvcBuilders.standaloneSetup(new RateLimitTestController())
            .addFilters(new GatewayRateLimitFilter(properties))
            .build();
  }

  @Test
  void allowsRequestsUntilWindowLimitIsReached() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/customers/context")
                .with(
                    request -> {
                      request.setRemoteAddr("10.0.0.1");
                      return request;
                    }))
        .andExpect(status().isOk())
        .andExpect(header().string("X-RateLimit-Limit", "2"))
        .andExpect(header().string("X-RateLimit-Remaining", "1"));

    mockMvc
        .perform(
            get("/api/v1/customers/context")
                .with(
                    request -> {
                      request.setRemoteAddr("10.0.0.1");
                      return request;
                    }))
        .andExpect(status().isOk())
        .andExpect(header().string("X-RateLimit-Remaining", "0"));
  }

  @Test
  void rejectsRequestsAboveWindowLimit() throws Exception {
    for (int i = 0; i < 2; i++) {
      mockMvc.perform(
          get("/api/v1/customers/context")
              .with(
                  request -> {
                    request.setRemoteAddr("10.0.0.2");
                    return request;
                  }));
    }

    mockMvc
        .perform(
            get("/api/v1/customers/context")
                .with(
                    request -> {
                      request.setRemoteAddr("10.0.0.2");
                      return request;
                    }))
        .andExpect(status().isTooManyRequests())
        .andExpect(header().exists("Retry-After"))
        .andExpect(jsonPath("$.message").value("Rate limit exceeded"));
  }

  @Test
  void skipsActuatorEndpoints() throws Exception {
    for (int i = 0; i < 3; i++) {
      mockMvc
          .perform(
              get("/actuator/health")
                  .with(
                      request -> {
                        request.setRemoteAddr("10.0.0.3");
                        return request;
                      }))
          .andExpect(status().isOk());
    }
  }

  @Test
  void ignoresForwardedForHeaderByDefault() throws Exception {
    for (int i = 0; i < 2; i++) {
      mockMvc.perform(
          get("/api/v1/customers/context")
              .with(
                  request -> {
                    request.setRemoteAddr("10.0.0.4");
                    request.addHeader("X-Forwarded-For", "203.0.113.10");
                    return request;
                  }));
    }

    mockMvc
        .perform(
            get("/api/v1/customers/context")
                .with(
                    request -> {
                      request.setRemoteAddr("10.0.0.4");
                      request.addHeader("X-Forwarded-For", "198.51.100.25");
                      return request;
                    }))
        .andExpect(status().isTooManyRequests());
  }

  @Test
  void usesForwardedForHeaderWhenExplicitlyEnabled() throws Exception {
    GatewayRateLimitProperties properties = new GatewayRateLimitProperties();
    properties.setEnabled(true);
    properties.setUseForwardedFor(true);
    properties.setRequestsPerWindow(1);
    properties.setWindowSeconds(60);

    MockMvc forwardedMockMvc =
        MockMvcBuilders.standaloneSetup(new RateLimitTestController())
            .addFilters(new GatewayRateLimitFilter(properties))
            .build();

    forwardedMockMvc
        .perform(
            get("/api/v1/customers/context")
                .with(
                    request -> {
                      request.setRemoteAddr("10.0.0.5");
                      request.addHeader("X-Forwarded-For", "203.0.113.10");
                      return request;
                    }))
        .andExpect(status().isOk());

    forwardedMockMvc
        .perform(
            get("/api/v1/customers/context")
                .with(
                    request -> {
                      request.setRemoteAddr("10.0.0.5");
                      request.addHeader("X-Forwarded-For", "198.51.100.25");
                      return request;
                    }))
        .andExpect(status().isOk());
  }

  @RestController
  static class RateLimitTestController {

    @GetMapping("/api/v1/customers/context")
    ResponseEntity<TestResponse> limitedEndpoint() {
      return ResponseEntity.ok(new TestResponse("ok"));
    }

    @GetMapping("/actuator/health")
    ResponseEntity<TestResponse> health() {
      return ResponseEntity.ok(new TestResponse("up"));
    }
  }

  record TestResponse(String message) {}
}
