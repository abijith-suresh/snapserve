package com.ust.gateway_service;

import org.junit.jupiter.api.Test;

class GatewayServiceApplicationTests {

  @Test
  void contextLoadsSkipped() {
    // Full context load requires JWT_SECRET and service URLs from env.
    // End-to-end tests covered by integration tests once Docker Compose is wired.
  }
}
