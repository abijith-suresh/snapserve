package com.snapserve.notificationclient.client;

import com.snapserve.notificationclient.request.SendNotificationRequest;
import com.snapserve.notificationclient.response.NotificationStatusResponse;
import com.snapserve.notificationclient.response.SendNotificationResponse;
import com.snapserve.notificationclient.response.TemplateResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${notification.service.url}")
public interface NotificationServiceClient {

  @PostMapping("/api/v1/notifications/send")
  SendNotificationResponse sendNotification(@Valid @RequestBody SendNotificationRequest request);

  @GetMapping("/api/v1/notifications/{notificationId}/status")
  NotificationStatusResponse getNotificationStatus(
      @PathVariable("notificationId") String notificationId);

  @GetMapping("/api/v1/notifications/templates")
  List<TemplateResponse> getTemplates();

  @GetMapping("/api/v1/notifications/templates/{name}")
  TemplateResponse getTemplateByName(@PathVariable("name") String name);
}
