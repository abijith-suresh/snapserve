package com.snapserve.notificationservice.dto.response;

import com.snapserve.notificationservice.model.NotificationStatus;
import com.snapserve.notificationservice.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationLogResponse {
    private String id;
    private NotificationType type;
    private String to;
    private String subject;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private String errorMessage;
}

