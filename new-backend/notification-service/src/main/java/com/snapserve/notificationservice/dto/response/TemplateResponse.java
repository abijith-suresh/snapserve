package com.snapserve.notificationservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateResponse {
    private String id;
    private String name;
    private String subject;
    private String content;
    private boolean active;
}

