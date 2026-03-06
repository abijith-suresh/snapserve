package com.snapserve.userclient.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,
    @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
        String phone,
    @Size(max = 500, message = "Address must not exceed 500 characters") String address,
    @Pattern(regexp = "^(CREDIT_CARD|PAYPAL|BANK_TRANSFER)$", message = "Invalid payment method")
        String preferredPaymentMethod) {}
