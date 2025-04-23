package com.snapserve.notificationservice.controller;

import com.snapserve.notificationservice.dto.response.NotificationLogResponse;
import com.snapserve.notificationservice.service.NotificationTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationLogController {

    private final NotificationTrackingService trackingService;

    @GetMapping("/{id}")
    public ResponseEntity<NotificationLogResponse> getNotificationById(@PathVariable String id) {
        return ResponseEntity.ok(trackingService.getNotificationById(id));
    }

    @GetMapping
    public ResponseEntity<List<NotificationLogResponse>> getAllNotifications() {
        return ResponseEntity.ok(trackingService.getAllNotifications());
    }
}
