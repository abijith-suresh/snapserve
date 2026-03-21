package com.snapserve.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.snapserve.auth.config.JwtTokenProvider;
import com.snapserve.auth.dto.AuthResponse;
import com.snapserve.auth.dto.LoginRequest;
import com.snapserve.auth.model.Account;
import com.snapserve.auth.repo.AccountRepo;
import com.snapserve.common.exception.AccountLockedException;
import com.snapserve.common.model.Role;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock private AccountRepo accountRepo;
  @Mock private JwtTokenProvider jwtTokenProvider;
  @Mock private RefreshTokenService refreshTokenService;
  @Mock private PasswordEncoder passwordEncoder;

  private AccountService accountService;

  @BeforeEach
  void setUp() {
    accountService = new AccountService(accountRepo, jwtTokenProvider, refreshTokenService);
    ReflectionTestUtils.setField(accountService, "passwordEncoder", passwordEncoder);
    ReflectionTestUtils.setField(accountService, "maxLoginAttempts", 5);
    ReflectionTestUtils.setField(accountService, "lockDurationMinutes", 15L);
  }

  @Test
  void loginKeepsAccountLockedDuringCooldownWindow() {
    Account account = lockedAccount(Instant.now().minus(Duration.ofMinutes(5)));
    LoginRequest request = loginRequest(account.getEmail(), "secret");
    when(accountRepo.findByEmail(account.getEmail())).thenReturn(Optional.of(account));

    assertThatThrownBy(() -> accountService.login(request, "device-1", "127.0.0.1"))
        .isInstanceOf(AccountLockedException.class)
        .hasMessageContaining("Account is locked");

    verify(accountRepo, never()).save(any(Account.class));
    verifyNoInteractions(jwtTokenProvider, refreshTokenService, passwordEncoder);
  }

  @Test
  void loginAutomaticallyUnlocksAccountAfterCooldownExpires() {
    Account account = lockedAccount(Instant.now().minus(Duration.ofMinutes(16)));
    LoginRequest request = loginRequest(account.getEmail(), "secret");

    when(accountRepo.findByEmail(account.getEmail())).thenReturn(Optional.of(account));
    when(passwordEncoder.matches("secret", account.getPassword())).thenReturn(true);
    when(jwtTokenProvider.createToken(account.getEmail(), account.getRole()))
        .thenReturn("access-token");
    when(refreshTokenService.createRefreshToken(account.getId(), "device-1", "127.0.0.1"))
        .thenReturn("refresh-token");
    when(jwtTokenProvider.getExpirationMs()).thenReturn(3_600_000L);

    AuthResponse response = accountService.login(request, "device-1", "127.0.0.1");

    ArgumentCaptor<Account> savedAccount = ArgumentCaptor.forClass(Account.class);
    verify(accountRepo).save(savedAccount.capture());

    assertThat(savedAccount.getValue().isLocked()).isFalse();
    assertThat(savedAccount.getValue().getFailedLoginAttempts()).isZero();
    assertThat(savedAccount.getValue().getLastFailedLoginAt()).isNull();
    assertThat(response.getAccessToken()).isEqualTo("access-token");
    assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
    assertThat(response.getExpiresIn()).isEqualTo(3600L);
  }

  private static LoginRequest loginRequest(String email, String password) {
    LoginRequest request = new LoginRequest();
    request.setEmail(email);
    request.setPassword(password);
    return request;
  }

  private static Account lockedAccount(Instant lastFailedLoginAt) {
    Account account = new Account();
    account.setId(new ObjectId());
    account.setEmail("locked@snapserve.com");
    account.setPassword("encoded-password");
    account.setRole(Role.CUSTOMER);
    account.setEnabled(true);
    account.setLocked(true);
    account.setFailedLoginAttempts(5);
    account.setLastFailedLoginAt(lastFailedLoginAt);
    return account;
  }
}
