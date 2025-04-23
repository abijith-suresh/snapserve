package com.snapserve.notificationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;
}

