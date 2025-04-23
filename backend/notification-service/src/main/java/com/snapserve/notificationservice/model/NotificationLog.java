package com.snapserve.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationLog {

    @Id
    private String id;

    private NotificationType type;
    private NotificationStatus status;

    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> variables;

    private LocalDateTime sentAt;
    private String errorMessage;
}
