package com.snapserve.notification.service;

import com.snapserve.notification.model.NotificationHistory;
import com.snapserve.notification.model.NotificationRetry;
import com.snapserve.notification.repository.NotificationRetryRepository;
import com.snapserve.notification.strategy.EmailNotificationStrategy;
import com.snapserve.notification.strategy.NotificationChannelFactory;
import com.snapserve.notificationclient.request.SendNotificationRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryService {

  private final NotificationRetryRepository retryRepository;
  private final NotificationHistoryService historyService;
  private final TemplateService templateService;
  private final NotificationChannelFactory channelFactory;
  private final EmailNotificationStrategy emailStrategy;

  @Value("${notification.retry.max-attempts:5}")
  private int maxRetries;

  @Value("${notification.retry.backoff-multiplier:2}")
  private int backoffMultiplier;

  @Value("${notification.retry.initial-delay-seconds:1}")
  private int initialDelaySeconds;

  @Value("${notification.retry.max-delay-seconds:60}")
  private int maxDelaySeconds;

  public void scheduleRetry(ObjectId notificationId, int currentAttempt) {
    if (currentAttempt >= maxRetries) {
      log.warn("Max retries reached for notification: {}. Marking as FAILED.", notificationId);
      historyService.markAsFailed(notificationId, "Max retries exceeded");
      return;
    }

    int delaySeconds =
        Math.min(
            initialDelaySeconds * (int) Math.pow(backoffMultiplier, currentAttempt),
            maxDelaySeconds);

    NotificationRetry retry = new NotificationRetry();
    retry.setId(new ObjectId());
    retry.setNotificationId(notificationId);
    retry.setNextRetryAt(Instant.now().plus(delaySeconds, ChronoUnit.SECONDS));
    retry.setRetryAttempt(currentAttempt + 1);
    retry.setMaxRetries(maxRetries);

    retryRepository.save(retry);
    historyService.markAsRetrying(notificationId);

    log.info(
        "Scheduled retry {} for notification: {} in {} seconds",
        currentAttempt + 1,
        notificationId,
        delaySeconds);
  }

  @Scheduled(fixedDelay = 30000) // Run every 30 seconds
  public void processRetries() {
    List<NotificationRetry> retriesToProcess =
        retryRepository.findByNextRetryAtLessThanEqual(Instant.now());

    for (NotificationRetry retry : retriesToProcess) {
      processRetry(retry);
    }
  }

  private void processRetry(NotificationRetry retry) {
    try {
      NotificationHistory history = historyService.getHistory(retry.getNotificationId());

      // TODO: Add rate limiting here
      // Check if recipient has exceeded rate limit before processing

      var template = templateService.getTemplate(history.getTemplateName(), history.getChannel());

      if (history.getChannel().name().equals("EMAIL")) {
        emailStrategy.sendEmail(
            history.getRecipient(),
            templateService.processTextTemplate(template.getSubject(), history.getParameters()),
            template.getBodyHtml(),
            history.getParameters());
      } else {
        channelFactory
            .getStrategy(history.getChannel())
            .send(
                SendNotificationRequest.builder()
                    .templateName(history.getTemplateName())
                    .channel(history.getChannel())
                    .recipient(history.getRecipient())
                    .parameters(history.getParameters())
                    .build());
      }

      // Success - mark as sent and delete retry record
      historyService.markAsSent(retry.getNotificationId());
      retryRepository.delete(retry);
      log.info("Retry successful for notification: {}", retry.getNotificationId());

    } catch (Exception e) {
      log.error("Retry failed for notification: {}", retry.getNotificationId(), e);
      retryRepository.delete(retry);
      scheduleRetry(retry.getNotificationId(), retry.getRetryAttempt());
    }
  }
}
