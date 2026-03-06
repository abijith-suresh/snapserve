package com.snapserve.auth.service;

import com.snapserve.auth.config.JwtTokenProvider;
import com.snapserve.auth.dto.AuthResponse;
import com.snapserve.auth.dto.LoginRequest;
import com.snapserve.auth.dto.RefreshTokenRequest;
import com.snapserve.auth.dto.RegisterRequest;
import com.snapserve.auth.model.Account;
import com.snapserve.auth.repo.AccountRepo;
import com.snapserve.common.exception.AccountLockedException;
import com.snapserve.common.exception.BadRequestException;
import com.snapserve.common.exception.ConflictException;
import com.snapserve.common.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${account.lockout.max-attempts:5}")
    private int maxLoginAttempts;

    public void register(RegisterRequest request) {
        if (accountRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Account with email " + request.getEmail() + " already exists");
        }
        Account account = toAccount(request);
        accountRepo.save(account);
        log.info("New account registered: {}", request.getEmail());
    }

    public AuthResponse login(LoginRequest request, String deviceId, String ipAddress) {
        Optional<Account> accountOpt = accountRepo.findByEmail(request.getEmail());

        if (accountOpt.isEmpty()) {
            log.warn("Failed login attempt for non-existent email: {}", request.getEmail());
            throw new BadRequestException("Invalid credentials");
        }

        Account account = accountOpt.get();

        if (account.isLocked()) {
            log.warn("Locked account login attempt: {}", request.getEmail());
            throw new AccountLockedException("Account is locked. Please try again later.");
        }

        if (!account.isEnabled()) {
            log.warn("Disabled account login attempt: {}", request.getEmail());
            throw new BadRequestException("Account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            handleFailedLogin(account);
            throw new BadRequestException("Invalid credentials");
        }

        resetFailedLoginAttempts(account);
        String accessToken = jwtTokenProvider.createToken(account.getEmail(), account.getRole());
        String refreshToken = refreshTokenService.createRefreshToken(account.getId(), deviceId, ipAddress);

        log.info("Successful login for email: {}", request.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(account.getRole())
                .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                .build();
    }

    public AuthResponse refreshAccessToken(RefreshTokenRequest request, String deviceId, String ipAddress) {
        Account account = refreshTokenService.getAccountFromRefreshToken(request.getRefreshToken());

        if (!account.isEnabled() || account.isLocked()) {
            throw new BadRequestException("Account is disabled or locked");
        }

        String accessToken = jwtTokenProvider.createToken(account.getEmail(), account.getRole());
        String newRefreshToken = refreshTokenService.createRefreshToken(account.getId(), deviceId, ipAddress);

        refreshTokenService.revokeRefreshToken(request.getRefreshToken());

        log.info("Token refreshed for email: {}", account.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .role(account.getRole())
                .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                .build();
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
        log.info("User logged out");
    }

    private Account toAccount(RegisterRequest request) {
        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(mapRole(request.getRole()));
        account.setEnabled(true);
        account.setLocked(false);
        account.setFailedLoginAttempts(0);
        return account;
    }

    private Role mapRole(String roleStr) {
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.CUSTOMER;
        }
    }

    private void handleFailedLogin(Account account) {
        account.setFailedLoginAttempts(account.getFailedLoginAttempts() + 1);
        account.setLastFailedLoginAt(Instant.now());

        if (account.getFailedLoginAttempts() >= maxLoginAttempts) {
            account.setLocked(true);
            log.warn("Account locked due to too many failed attempts: {}", account.getEmail());
        }

        accountRepo.save(account);
        log.warn("Failed login attempt: {} (attempts: {})", account.getEmail(), account.getFailedLoginAttempts());
    }

    private void resetFailedLoginAttempts(Account account) {
        if (account.getFailedLoginAttempts() > 0) {
            account.setFailedLoginAttempts(0);
            account.setLastFailedLoginAt(null);
            accountRepo.save(account);
        }
    }
}
