package com.snapserve.notification.controller;

import com.snapserve.common.mongo.ObjectIdParser;
import com.snapserve.notification.model.NotificationHistory;
import com.snapserve.notification.model.NotificationTemplate;
import com.snapserve.notification.service.NotificationHistoryService;
import com.snapserve.notification.service.NotificationOrchestrator;
import com.snapserve.notification.service.TemplateService;
import com.snapserve.notificationclient.request.SendNotificationRequest;
import com.snapserve.notificationclient.response.NotificationStatusResponse;
import com.snapserve.notificationclient.response.SendNotificationResponse;
import com.snapserve.notificationclient.response.TemplateResponse;
import jakarta.validation.Valid;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationOrchestrator notificationOrchestrator;
  private final NotificationHistoryService historyService;
  private final TemplateService templateService;

  @PostMapping("/send")
  public ResponseEntity<SendNotificationResponse> sendNotification(
      @Valid @RequestBody SendNotificationRequest request) {

    ObjectId notificationId = notificationOrchestrator.sendNotification(request);

    SendNotificationResponse response =
        SendNotificationResponse.builder()
            .notificationId(notificationId.toString())
            .status("ACCEPTED")
            .message("Notification queued for delivery")
            .build();

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }

  @GetMapping("/{notificationId}/status")
  public ResponseEntity<NotificationStatusResponse> getNotificationStatus(
      @PathVariable String notificationId) {

    NotificationHistory history =
        historyService.getHistory(ObjectIdParser.parse(notificationId, "notification"));

    NotificationStatusResponse response =
        NotificationStatusResponse.builder()
            .notificationId(history.getId().toString())
            .templateName(history.getTemplateName())
            .recipient(history.getRecipient())
            .channel(history.getChannel())
            .status(history.getStatus())
            .parameters(history.getParameters())
            .sentAt(
                history.getSentAt() != null
                    ? history.getSentAt().atZone(ZoneId.systemDefault()).toInstant()
                    : null)
            .retryCount(history.getRetryCount())
            .errorMessage(history.getErrorMessage())
            .createdAt(
                history.getCreatedAt() != null
                    ? history.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
                    : null)
            .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/templates")
  public ResponseEntity<List<TemplateResponse>> getTemplates() {
    List<NotificationTemplate> templates = templateService.getAllTemplates();

    List<TemplateResponse> response =
        templates.stream()
            .map(
                t ->
                    TemplateResponse.builder()
                        .id(t.getId().toString())
                        .name(t.getName())
                        .channel(t.getChannel())
                        .subject(t.getSubject())
                        .version(String.valueOf(t.getVersion()))
                        .active(t.getActive())
                        .createdAt(
                            t.getCreatedAt() != null
                                ? t.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
                                : null)
                        .updatedAt(
                            t.getUpdatedAt() != null
                                ? t.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()
                                : null)
                        .build())
            .toList();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/templates/{name}")
  public ResponseEntity<TemplateResponse> getTemplateByName(@PathVariable String name) {
    NotificationTemplate template = templateService.getTemplateByName(name);

    TemplateResponse response =
        TemplateResponse.builder()
            .id(template.getId().toString())
            .name(template.getName())
            .channel(template.getChannel())
            .subject(template.getSubject())
            .version(String.valueOf(template.getVersion()))
            .active(template.getActive())
            .createdAt(
                template.getCreatedAt() != null
                    ? template.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
                    : null)
            .updatedAt(
                template.getUpdatedAt() != null
                    ? template.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()
                    : null)
            .build();

    return ResponseEntity.ok(response);
  }

  @PostMapping("/templates")
  public ResponseEntity<TemplateResponse> createTemplate(
      @RequestBody NotificationTemplate template) {
    NotificationTemplate created = templateService.createTemplate(template);

    TemplateResponse response =
        TemplateResponse.builder()
            .id(created.getId().toString())
            .name(created.getName())
            .channel(created.getChannel())
            .subject(created.getSubject())
            .version(String.valueOf(created.getVersion()))
            .active(created.getActive())
            .createdAt(
                created.getCreatedAt() != null
                    ? created.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
                    : null)
            .updatedAt(
                created.getUpdatedAt() != null
                    ? created.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()
                    : null)
            .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/templates/{name}")
  public ResponseEntity<TemplateResponse> updateTemplate(
      @PathVariable String name, @RequestBody NotificationTemplate template) {
    NotificationTemplate updated = templateService.updateTemplate(name, template);

    TemplateResponse response =
        TemplateResponse.builder()
            .id(updated.getId().toString())
            .name(updated.getName())
            .channel(updated.getChannel())
            .subject(updated.getSubject())
            .version(String.valueOf(updated.getVersion()))
            .active(updated.getActive())
            .createdAt(
                updated.getCreatedAt() != null
                    ? updated.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
                    : null)
            .updatedAt(
                updated.getUpdatedAt() != null
                    ? updated.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()
                    : null)
            .build();

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/templates/{name}")
  public ResponseEntity<Void> deleteTemplate(@PathVariable String name) {
    templateService.deleteTemplate(name);
    return ResponseEntity.noContent().build();
  }
}
