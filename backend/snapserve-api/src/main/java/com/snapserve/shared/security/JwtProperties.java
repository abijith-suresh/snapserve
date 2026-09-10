package com.snapserve.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(String secret) {

  public JwtProperties {
    if (secret == null || secret.isBlank()) {
      throw new IllegalArgumentException("security.jwt.secret must be configured");
    }
    if (secret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 64) {
      throw new IllegalArgumentException("security.jwt.secret must be at least 64 bytes");
    }
  }
}
