package com.snapserve.reviewservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotBlank
    private String customerId;

    @NotBlank
    private String specialistId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(max = 1000)
    private String comment;
}
