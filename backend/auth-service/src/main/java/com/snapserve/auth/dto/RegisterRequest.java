package com.snapserve.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Password is required")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message =
          "Password must be at least 8 characters, include uppercase, lowercase, digit, and special character")
  private String password;

  @NotBlank(message = "Role is required")
  @Pattern(
      regexp = "CUSTOMER|SPECIALIST",
      flags = Pattern.Flag.CASE_INSENSITIVE,
      message = "Role must be one of: CUSTOMER, SPECIALIST")
  private String role;
}
