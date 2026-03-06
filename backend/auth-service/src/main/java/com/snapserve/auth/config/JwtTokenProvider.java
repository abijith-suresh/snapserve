package com.snapserve.auth.config;

import com.snapserve.common.jwt.JwtUtils;
import com.snapserve.common.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
  private final JwtUtils jwtUtils;
  private final PasswordEncoder passwordEncoder;

  @Value("${jwt.expiration-ms:3600000}")
  private long expirationMs;

  public String createToken(String email, Role role) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(email)
        .issuedAt(new Date(now))
        .expiration(new Date(now + expirationMs))
        .claim("role", role.name())
        .signWith(jwtUtils.getSigningKey())
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(jwtUtils.getSigningKey()).build().parseSignedClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.warn("Token is expired");
      throw new RuntimeException("Token is expired");
    } catch (Exception e) {
      log.warn("Invalid token: {}", e.getMessage());
      throw new RuntimeException("Invalid token");
    }
  }

  public Claims getClaimsFromToken(String token) {
    return jwtUtils.extractClaims(token);
  }

  public String getUsernameFromToken(String token) {
    return getClaimsFromToken(token).getSubject();
  }

  public Role getRoleFromToken(String token) {
    return Role.valueOf(getClaimsFromToken(token).get("role", String.class));
  }

  public long getExpirationMs() {
    return expirationMs;
  }

  public PasswordEncoder passwordEncoder() {
    return passwordEncoder;
  }
}
