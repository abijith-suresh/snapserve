package com.snapserve.notificationservice.service;

import com.snapserve.notificationservice.dto.request.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationManager {
    private final NotificationService emailService;

    public boolean sendNotification(NotificationRequest request) {
        return switch (request.getType()) {
            case EMAIL -> emailService.sendNotification(request);
            default -> throw new UnsupportedOperationException("Notification type not supported: " + request.getType());
        };
    }
}
