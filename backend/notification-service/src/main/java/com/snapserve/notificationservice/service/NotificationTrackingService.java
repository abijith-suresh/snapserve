package com.snapserve.notificationservice.service;

import com.snapserve.notificationservice.dto.response.NotificationLogResponse;
import com.snapserve.notificationservice.exception.ResourceNotFoundException;
import com.snapserve.notificationservice.mapper.NotificationLogMapper;
import com.snapserve.notificationservice.model.NotificationLog;
import com.snapserve.notificationservice.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationTrackingService {

    private final NotificationLogRepository logRepository;

    public NotificationLogResponse getNotificationById(String id) {
        NotificationLog log = logRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + id));
        return NotificationLogMapper.toResponse(log);
    }

    public List<NotificationLogResponse> getAllNotifications() {
        List<NotificationLog> logs = logRepository.findAll();
        return logs.stream()
                .map(NotificationLogMapper::toResponse)
                .collect(Collectors.toList());
    }
}

