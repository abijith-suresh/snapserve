package com.snapserve.notification.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snapserve.notification.model.NotificationHistory;
import com.snapserve.notification.model.NotificationRetry;
import com.snapserve.notification.model.NotificationTemplate;
import com.snapserve.notification.repository.NotificationRetryRepository;
import com.snapserve.notification.strategy.EmailNotificationStrategy;
import com.snapserve.notification.strategy.NotificationChannelFactory;
import com.snapserve.notification.strategy.NotificationChannelStrategy;
import com.snapserve.notificationclient.constants.NotificationChannel;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RetryServiceTest {

  @Mock private NotificationRetryRepository retryRepository;
  @Mock private NotificationHistoryService historyService;
  @Mock private TemplateService templateService;
  @Mock private NotificationChannelFactory channelFactory;
  @Mock private EmailNotificationStrategy emailStrategy;
  @Mock private NotificationChannelStrategy notificationChannelStrategy;

  private RetryService retryService;

  @BeforeEach
  void setUp() throws Exception {
    Constructor<RetryService> constructor =
        RetryService.class.getDeclaredConstructor(
            NotificationRetryRepository.class,
            NotificationHistoryService.class,
            TemplateService.class,
            NotificationChannelFactory.class,
            EmailNotificationStrategy.class);
    constructor.setAccessible(true);
    retryService =
        constructor.newInstance(
            retryRepository, historyService, templateService, channelFactory, emailStrategy);
    ReflectionTestUtils.setField(retryService, "maxRetries", 5);
    ReflectionTestUtils.setField(retryService, "backoffMultiplier", 2);
    ReflectionTestUtils.setField(retryService, "initialDelaySeconds", 1);
    ReflectionTestUtils.setField(retryService, "maxDelaySeconds", 60);
  }

  @Test
  void processRetriesDelegatesNonEmailRetriesToConfiguredStrategy() throws Exception {
    ObjectId notificationId = new ObjectId();
    NotificationTemplate template = new NotificationTemplate();

    NotificationRetry retry = new NotificationRetry();
    ReflectionTestUtils.setField(retry, "id", new ObjectId());
    ReflectionTestUtils.setField(retry, "notificationId", notificationId);
    ReflectionTestUtils.setField(retry, "retryAttempt", 1);
    ReflectionTestUtils.setField(retry, "nextRetryAt", Instant.now().minusSeconds(5));

    NotificationHistory history = new NotificationHistory();
    ReflectionTestUtils.setField(history, "id", notificationId);
    ReflectionTestUtils.setField(history, "templateName", "booking-reminder");
    ReflectionTestUtils.setField(history, "channel", NotificationChannel.SMS);
    ReflectionTestUtils.setField(history, "recipient", "+15555550104");
    ReflectionTestUtils.setField(history, "parameters", Map.of("customerName", "Jamie"));

    when(retryRepository.findByNextRetryAtLessThanEqual(
            org.mockito.ArgumentMatchers.any(Instant.class)))
        .thenReturn(List.of(retry));
    when(historyService.getHistory(notificationId)).thenReturn(history);
    when(templateService.getTemplate("booking-reminder", NotificationChannel.SMS))
        .thenReturn(template);
    when(channelFactory.getStrategy(NotificationChannel.SMS))
        .thenReturn(notificationChannelStrategy);

    retryService.processRetries();

    verify(notificationChannelStrategy)
        .send(
            org.mockito.ArgumentMatchers.argThat(
                request ->
                    ReflectionTestUtils.getField(request, "templateName").equals("booking-reminder")
                        && ReflectionTestUtils.getField(request, "channel")
                            == NotificationChannel.SMS
                        && ReflectionTestUtils.getField(request, "recipient").equals("+15555550104")
                        && ReflectionTestUtils.getField(request, "parameters")
                            .equals(Map.of("customerName", "Jamie"))));
    verify(historyService).markAsSent(notificationId);
    verify(retryRepository).delete(retry);
  }
}
