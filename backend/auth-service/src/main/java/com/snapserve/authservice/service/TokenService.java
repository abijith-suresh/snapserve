package com.snapserve.authservice.service;

import com.snapserve.authservice.model.AuthUser;

public interface TokenService {
    String generateAccessToken(String email);
    String generateRefreshToken(String email);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
}

