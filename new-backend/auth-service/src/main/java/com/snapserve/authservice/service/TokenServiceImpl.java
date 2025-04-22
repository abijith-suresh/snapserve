package com.snapserve.authservice.service;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {

    private final String SECRET_KEY = "mySecretKey";
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1 hour
    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 30; // 30 days

    @Override
    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_TOKEN_EXPIRATION);
    }

    @Override
    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_TOKEN_EXPIRATION);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    private String generateToken(String email, long expirationTime) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
}
