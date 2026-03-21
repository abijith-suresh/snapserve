package com.snapserve.auth.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.snapserve.common.jwt.JwtUtils;
import com.snapserve.common.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderTest {

  private JwtUtils jwtUtils;
  private JwtTokenProvider jwtTokenProvider;

  @BeforeEach
  void setUp() {
    jwtUtils = new JwtUtils();
    ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "01234567890123456789012345678901");
    jwtUtils.init();

    PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
    jwtTokenProvider = new JwtTokenProvider(jwtUtils, passwordEncoder);
    ReflectionTestUtils.setField(jwtTokenProvider, "expirationMs", 3_600_000L);
  }

  @Test
  void createTokenStoresSubjectAndSingleRoleClaim() {
    String token = jwtTokenProvider.createToken("specialist@snapserve.com", Role.SPECIALIST);

    assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("specialist@snapserve.com");
    assertThat(jwtTokenProvider.getClaimsFromToken(token).get("role", String.class))
        .isEqualTo(Role.SPECIALIST.name());
  }
}
