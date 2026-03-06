package com.snapserve.auth.service;

import com.snapserve.auth.model.Account;
import com.snapserve.auth.model.RefreshToken;
import com.snapserve.auth.repo.AccountRepo;
import com.snapserve.auth.repo.RefreshTokenRepo;
import com.snapserve.common.exception.InvalidRefreshTokenException;
import com.snapserve.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final AccountRepo accountRepo;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    public String createRefreshToken(ObjectId userId, String deviceId, String ipAddress) {
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusMillis(refreshTokenExpirationMs);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .deviceId(deviceId)
                .ipAddress(ipAddress)
                .expiresAt(expiresAt)
                .createdAt(Instant.now())
                .build();

        refreshTokenRepo.save(refreshToken);
        log.info("Created refresh token for user: {}", userId);
        return token;
    }

    public RefreshToken validateRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepo.findByToken(token);
        if (refreshToken.isEmpty()) {
            log.warn("Invalid refresh token attempted");
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        RefreshToken rt = refreshToken.get();
        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            log.warn("Expired or revoked refresh token for user: {}", rt.getUserId());
            throw new InvalidRefreshTokenException("Refresh token expired or revoked");
        }

        return rt;
    }

    public void revokeRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepo.findByToken(token);
        if (refreshToken.isPresent()) {
            RefreshToken rt = refreshToken.get();
            rt.setRevoked(true);
            refreshTokenRepo.save(rt);
            log.info("Revoked refresh token for user: {}", rt.getUserId());
        }
    }

    public void revokeAllUserTokens(ObjectId userId) {
        refreshTokenRepo.deleteByUserId(userId);
        log.info("Revoked all refresh tokens for user: {}", userId);
    }

    public Account getAccountFromRefreshToken(String token) {
        RefreshToken rt = validateRefreshToken(token);
        return accountRepo.findById(rt.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + rt.getUserId()));
    }
}