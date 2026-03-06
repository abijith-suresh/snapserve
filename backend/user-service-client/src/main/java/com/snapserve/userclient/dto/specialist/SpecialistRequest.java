package com.snapserve.userclient.dto.specialist;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record SpecialistRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,
    @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
        String phone,
    @NotBlank(message = "Title is required")
        @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
        String title,
    @NotEmpty(message = "At least one service is required")
        @Size(min = 1, max = 20, message = "Services must be between 1 and 20 items")
        List<@NotBlank(message = "Service cannot be blank") String> services,
    @Positive(message = "Hourly rate must be positive") BigDecimal hourlyRate) {}
