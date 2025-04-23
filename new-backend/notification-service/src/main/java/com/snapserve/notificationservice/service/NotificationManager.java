package com.snapserve.notificationservice.service;

import com.snapserve.notificationservice.dto.request.NotificationRequest;
import com.snapserve.notificationservice.model.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationManager {

    private final Map<NotificationType, NotificationService> notificationServiceMap;

    public NotificationManager(List<NotificationService> services) {
        this.notificationServiceMap = services.stream()
                .collect(Collectors.toMap(NotificationService::getNotificationType, service -> service));
    }

    public boolean sendNotification(NotificationRequest request) {
        NotificationService service = notificationServiceMap.get(request.getType());
        if (service == null) {
            throw new UnsupportedOperationException("Notification type not supported: " + request.getType());
        }
        return service.sendNotification(request);
    }
}