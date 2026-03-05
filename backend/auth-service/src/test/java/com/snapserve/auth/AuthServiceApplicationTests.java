package com.snapserve.auth;

import org.junit.jupiter.api.Test;

class AuthServiceApplicationTests {

  @Test
  void contextLoadsSkipped() {
    // Full context load requires JWT_SECRET, MongoDB, and other env vars.
    // End-to-end tests covered by integration tests once Docker Compose is wired.
  }
}
