package com.snapserve.notification.service;

import com.snapserve.notification.model.NotificationHistory;
import com.snapserve.notification.model.NotificationStatus;
import com.snapserve.notification.repository.NotificationHistoryRepository;
import com.snapserve.notificationclient.constants.NotificationChannel;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationHistoryService {

  private final NotificationHistoryRepository historyRepository;

  public NotificationHistory createHistory(
      String templateName,
      String recipient,
      NotificationChannel channel,
      Map<String, Object> parameters) {

    NotificationHistory history = new NotificationHistory();
    history.setId(new ObjectId());
    history.setTemplateName(templateName);
    history.setRecipient(recipient);
    history.setChannel(channel);
    history.setStatus(NotificationStatus.PENDING);
    history.setParameters(parameters);
    history.setRetryCount(0);

    return historyRepository.save(history);
  }

  public void markAsSent(ObjectId notificationId) {
    NotificationHistory history = historyRepository.findById(notificationId).orElseThrow();
    history.setStatus(NotificationStatus.SENT);
    history.setSentAt(Instant.now());
    historyRepository.save(history);
  }

  public void markAsFailed(ObjectId notificationId, String errorMessage) {
    NotificationHistory history = historyRepository.findById(notificationId).orElseThrow();
    history.setStatus(NotificationStatus.FAILED);
    history.setErrorMessage(errorMessage);
    historyRepository.save(history);
  }

  public void markAsRetrying(ObjectId notificationId) {
    NotificationHistory history = historyRepository.findById(notificationId).orElseThrow();
    history.setStatus(NotificationStatus.RETRYING);
    history.setRetryCount(history.getRetryCount() + 1);
    historyRepository.save(history);
  }

  public NotificationHistory getHistory(ObjectId notificationId) {
    return historyRepository
        .findById(notificationId)
        .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
  }
}
