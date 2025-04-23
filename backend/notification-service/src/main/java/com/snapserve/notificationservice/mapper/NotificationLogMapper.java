package com.snapserve.notificationservice.mapper;

import com.snapserve.notificationservice.dto.response.NotificationLogResponse;
import com.snapserve.notificationservice.model.NotificationLog;

public class NotificationLogMapper {

    public static NotificationLogResponse toResponse(NotificationLog log) {
        return NotificationLogResponse.builder()
                .id(log.getId())
                .type(log.getType())
                .to(log.getTo())
                .subject(log.getSubject())
                .status(log.getStatus())
                .sentAt(log.getSentAt())
                .errorMessage(log.getErrorMessage())
                .build();
    }
}

