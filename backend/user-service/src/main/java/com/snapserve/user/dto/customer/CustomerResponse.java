package com.snapserve.user.dto.customer;

import java.time.Instant;

public record CustomerResponse(
    String id,
    String email,
    String name,
    String phone,
    String address,
    String preferredPaymentMethod,
    Instant createdAt,
    Instant updatedAt) {}
