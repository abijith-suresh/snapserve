package com.snapserve.notificationservice.service;

import com.snapserve.notificationservice.dto.request.NotificationRequest;
import com.snapserve.notificationservice.model.NotificationType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationManager {

    private final Map<String, NotificationService> notificationServiceBeans;
    private final Map<NotificationType, NotificationService> notificationServiceMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (NotificationService service : notificationServiceBeans.values()) {
            notificationServiceMap.put(service.getNotificationType(), service);
        }
    }


    public boolean sendNotification(NotificationRequest request) {
        NotificationService service = notificationServiceMap.get(request.getType());
        if (service == null) {
            throw new UnsupportedOperationException("Notification type not supported: " + request.getType());
        }
        return service.sendNotification(request);
    }
}