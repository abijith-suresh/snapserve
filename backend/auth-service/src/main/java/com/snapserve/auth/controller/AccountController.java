package com.snapserve.auth.controller;

import com.snapserve.auth.dto.LoginDto;
import com.snapserve.auth.dto.LoginResponse;
import com.snapserve.auth.dto.RegisterDto;
import com.snapserve.auth.service.AccountService;
import com.snapserve.common.response.ApiResponse;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AccountController {

  @Autowired private AccountService accountService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterDto registerDto) {
    accountService.register(registerDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("User registered successfully"));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginDto loginDto) {
    String token = accountService.login(loginDto);
    return ResponseEntity.ok(ApiResponse.ok(new LoginResponse(token)));
  }

  @GetMapping("/validate/token")
  public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestParam String token) {
    return ResponseEntity.ok(ApiResponse.ok(accountService.verify(token)));
  }

  @GetMapping("/extract/roles")
  public ResponseEntity<ApiResponse<Map<String, String>>> extractRolesFromToken(
      @RequestParam String token) {
    accountService.verify(token);
    String roles = accountService.getRolesFromToken(token);
    return ResponseEntity.ok(ApiResponse.ok(Map.of("roles", roles)));
  }
}
