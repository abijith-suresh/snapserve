package com.ust.auth_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration-ms}")
  private long expirationMs;

  private SecretKey signingKey;

  @PostConstruct
  public void init() {
    this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  public String createToken(String email, String roles) {
    long now = System.currentTimeMillis();
    Date expiryDate = new Date(now + expirationMs);

    return Jwts.builder()
        .subject(email)
        .issuedAt(new Date(now))
        .expiration(expiryDate)
        .claim("roles", roles)
        .signWith(signingKey)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
      return true;
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      throw new RuntimeException("Token is expired");
    } catch (Exception e) {
      throw new RuntimeException("Invalid token");
    }
  }

  public Claims getClaimsFromToken(String token) {
    return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
  }

  public String getUsernameFromToken(String token) {
    Claims claims = getClaimsFromToken(token);
    return claims.getSubject();
  }
}
