package com.snapserve.notificationservice.service;

import com.snapserve.notificationservice.dto.request.NotificationRequest;

public interface NotificationService {
    boolean sendNotification(NotificationRequest request);
}

