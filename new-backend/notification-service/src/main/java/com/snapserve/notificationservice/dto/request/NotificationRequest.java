package com.snapserve.notificationservice.dto.request;

import com.snapserve.notificationservice.model.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class NotificationRequest {
    @NotNull
    private NotificationType type;

    @NotNull
    private String to;

    @NotNull
    private String subject;

    @NotNull
    private String templateName;

    private Map<String, Object> variables;
}
