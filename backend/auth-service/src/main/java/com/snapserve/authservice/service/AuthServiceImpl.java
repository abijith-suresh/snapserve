package com.snapserve.authservice.service;

import com.snapserve.authservice.client.UserServiceClient;
import com.snapserve.authservice.dto.request.LoginRequest;
import com.snapserve.authservice.dto.request.RefreshTokenRequest;
import com.snapserve.authservice.dto.request.RegisterRequest;
import com.snapserve.authservice.dto.response.AuthResponse;
import com.snapserve.authservice.exception.*;
import com.snapserve.authservice.mapper.UserMapper;
import com.snapserve.authservice.model.AuthUser;
import com.snapserve.authservice.model.VerificationToken;
import com.snapserve.authservice.repository.AuthUserRepository;
import com.snapserve.authservice.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserServiceClient userServiceClient;
    private final AuthUserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        switch (request.getRole()) {
            case "ADMIN" -> userServiceClient.createAdmin(UserMapper.toAdminCreateRequest(request));
            case "CUSTOMER" -> userServiceClient.createCustomer(UserMapper.toCustomerCreateRequest(request));
            case "SPECIALIST" -> userServiceClient.createSpecialist(UserMapper.toSpecialistCreateRequest(request));
            default -> throw new InvalidRoleException("Invalid role specified");
        }

        AuthUser user = new AuthUser();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(false);

        AuthUser savedUser = userRepository.save(user);

        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(Duration.ofHours(24));

        VerificationToken verificationToken = new VerificationToken(
                null,
                token,
                savedUser.getId(),
                expiry
        );
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        AuthUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        String accessToken = tokenService.generateAccessToken(user.getEmail());
        String refreshToken = tokenService.generateRefreshToken(user.getEmail());

        return new AuthResponse(accessToken, refreshToken, 3600);
    }

    @Override
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired token"));

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenExpiredException("Verification token has expired");
        }

        AuthUser user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!tokenService.validateToken(request.getRefreshToken())) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String email = tokenService.getEmailFromToken(request.getRefreshToken());
        AuthUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newAccessToken = tokenService.generateAccessToken(user.getEmail());
        String newRefreshToken = tokenService.generateRefreshToken(user.getEmail());

        return new AuthResponse(newAccessToken, newRefreshToken, 3600);
    }
}