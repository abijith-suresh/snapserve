package com.snapserve.notificationservice.service;

import com.snapserve.notificationservice.dto.request.NotificationRequest;
import com.snapserve.notificationservice.model.NotificationType;

public interface NotificationService {
    boolean sendNotification(NotificationRequest request);
    NotificationType getNotificationType();
}

