package com.snapserve.userclient.dto.specialist;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SpecialistResponse(
    String id,
    String email,
    String name,
    String phone,
    String title,
    List<String> services,
    BigDecimal hourlyRate,
    Boolean verified,
    Instant createdAt,
    Instant updatedAt) {}
