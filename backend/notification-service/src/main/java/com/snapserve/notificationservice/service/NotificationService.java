package com.snapserve.notificationservice.service;

import com.snapserve.notificationservice.dto.request.NotificationRequest;
import com.snapserve.notificationservice.model.NotificationType;
import org.springframework.stereotype.Component;

@Component
public interface NotificationService {
    boolean sendNotification(NotificationRequest request);
    NotificationType getNotificationType();
}

