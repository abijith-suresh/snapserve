package com.snapserve.notification.service;

import com.snapserve.notification.model.NotificationHistory;
import com.snapserve.notification.model.NotificationTemplate;
import com.snapserve.notification.strategy.EmailNotificationStrategy;
import com.snapserve.notification.strategy.NotificationChannelFactory;
import com.snapserve.notificationclient.constants.NotificationChannel;
import com.snapserve.notificationclient.request.SendNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationOrchestrator {

  private final TemplateService templateService;
  private final NotificationHistoryService historyService;
  private final RetryService retryService;
  private final EmailNotificationStrategy emailStrategy;
  private final NotificationChannelFactory channelFactory;

  @Transactional
  public ObjectId sendNotification(SendNotificationRequest request) {
    // TODO: Add idempotency check here
    // Check if a notification with same template, recipient, and parameters
    // was sent recently (e.g., within last 5 minutes) to prevent duplicates

    // TODO: Add rate limiting here
    // Check if recipient has exceeded rate limit before processing

    // Create history record
    NotificationHistory history =
        historyService.createHistory(
            request.getTemplateName(),
            request.getRecipient(),
            request.getChannel(),
            request.getParameters());

    try {
      // Get template
      NotificationTemplate template =
          templateService.getTemplate(request.getTemplateName(), request.getChannel());

      // Send notification based on channel
      if (request.getChannel() == NotificationChannel.EMAIL) {
        sendEmail(request, template);
      } else {
        channelFactory.getStrategy(request.getChannel()).send(request);
      }

      // Mark as sent
      historyService.markAsSent(history.getId());
      log.info(
          "Notification sent successfully: {} to {}",
          request.getTemplateName(),
          request.getRecipient());

    } catch (Exception e) {
      log.error("Failed to send notification: {}", request.getTemplateName(), e);
      historyService.markAsFailed(history.getId(), e.getMessage());
      retryService.scheduleRetry(history.getId(), 0);
    }

    return history.getId();
  }

  private void sendEmail(SendNotificationRequest request, NotificationTemplate template)
      throws Exception {
    emailStrategy.sendEmail(
        request.getRecipient(),
        template.getSubject(),
        template.getBodyHtml(),
        template,
        request.getParameters());
  }
}
