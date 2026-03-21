package com.snapserve.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snapserve.notification.model.NotificationHistory;
import com.snapserve.notification.model.NotificationTemplate;
import com.snapserve.notification.strategy.EmailNotificationStrategy;
import com.snapserve.notification.strategy.NotificationChannelFactory;
import com.snapserve.notification.strategy.NotificationChannelStrategy;
import com.snapserve.notificationclient.constants.NotificationChannel;
import com.snapserve.notificationclient.request.SendNotificationRequest;
import java.lang.reflect.Constructor;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationOrchestratorTest {

  @Mock private TemplateService templateService;
  @Mock private NotificationHistoryService historyService;
  @Mock private RetryService retryService;
  @Mock private EmailNotificationStrategy emailStrategy;
  @Mock private NotificationChannelFactory channelFactory;
  @Mock private NotificationChannelStrategy notificationChannelStrategy;

  private NotificationOrchestrator notificationOrchestrator;

  @BeforeEach
  void setUp() throws Exception {
    Constructor<NotificationOrchestrator> constructor =
        NotificationOrchestrator.class.getDeclaredConstructor(
            TemplateService.class,
            NotificationHistoryService.class,
            RetryService.class,
            EmailNotificationStrategy.class,
            NotificationChannelFactory.class);
    constructor.setAccessible(true);
    notificationOrchestrator =
        constructor.newInstance(
            templateService, historyService, retryService, emailStrategy, channelFactory);
  }

  @Test
  void sendNotificationDelegatesNonEmailChannelsToConfiguredStrategy() throws Exception {
    NotificationHistory history = new NotificationHistory();
    ObjectId notificationId = new ObjectId();
    ReflectionTestUtils.setField(history, "id", notificationId);

    NotificationTemplate template = new NotificationTemplate();

    SendNotificationRequest request = new SendNotificationRequest();
    ReflectionTestUtils.setField(request, "templateName", "booking-reminder");
    ReflectionTestUtils.setField(request, "channel", NotificationChannel.SMS);
    ReflectionTestUtils.setField(request, "recipient", "+15555550103");
    ReflectionTestUtils.setField(request, "parameters", Map.of("customerName", "Jamie"));

    when(historyService.createHistory(
            (String) ReflectionTestUtils.getField(request, "templateName"),
            (String) ReflectionTestUtils.getField(request, "recipient"),
            (NotificationChannel) ReflectionTestUtils.getField(request, "channel"),
            (Map<String, Object>) ReflectionTestUtils.getField(request, "parameters")))
        .thenReturn(history);
    when(templateService.getTemplate("booking-reminder", NotificationChannel.SMS))
        .thenReturn(template);
    when(channelFactory.getStrategy(NotificationChannel.SMS))
        .thenReturn(notificationChannelStrategy);

    notificationOrchestrator.sendNotification(request);

    verify(notificationChannelStrategy).send(request);
    verify(historyService).markAsSent(notificationId);
  }

  @Test
  void sendNotificationProcessesSubjectTemplateParametersForEmail() throws Exception {
    NotificationHistory history = new NotificationHistory();
    ObjectId notificationId = new ObjectId();
    ReflectionTestUtils.setField(history, "id", notificationId);

    NotificationTemplate template = new NotificationTemplate();
    ReflectionTestUtils.setField(template, "subject", "Booking Cancelled - {bookingId}");
    ReflectionTestUtils.setField(template, "bodyHtml", "<p th:text=\"${customerName}\">[Name]</p>");

    SendNotificationRequest request = new SendNotificationRequest();
    ReflectionTestUtils.setField(request, "templateName", "BOOKING_CANCELLATION");
    ReflectionTestUtils.setField(request, "channel", NotificationChannel.EMAIL);
    ReflectionTestUtils.setField(request, "recipient", "jamie@example.com");
    ReflectionTestUtils.setField(
        request, "parameters", Map.of("customerName", "Jamie", "bookingId", "booking-123"));

    when(historyService.createHistory(
            (String) ReflectionTestUtils.getField(request, "templateName"),
            (String) ReflectionTestUtils.getField(request, "recipient"),
            (NotificationChannel) ReflectionTestUtils.getField(request, "channel"),
            (Map<String, Object>) ReflectionTestUtils.getField(request, "parameters")))
        .thenReturn(history);
    when(templateService.getTemplate("BOOKING_CANCELLATION", NotificationChannel.EMAIL))
        .thenReturn(template);
    when(templateService.processTextTemplate(
            "Booking Cancelled - {bookingId}",
            Map.of("customerName", "Jamie", "bookingId", "booking-123")))
        .thenReturn("Booking Cancelled - booking-123");

    notificationOrchestrator.sendNotification(request);

    ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
    verify(emailStrategy)
        .sendEmail(
            org.mockito.ArgumentMatchers.eq("jamie@example.com"),
            subjectCaptor.capture(),
            org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.eq(
                Map.of("customerName", "Jamie", "bookingId", "booking-123")));
    assertThat(subjectCaptor.getValue()).isEqualTo("Booking Cancelled - booking-123");
    verify(historyService).markAsSent(notificationId);
  }

  @Test
  void processTextTemplateReplacesNamedPlaceholders() throws Exception {
    com.snapserve.notification.repository.NotificationTemplateRepository templateRepository =
        org.mockito.Mockito.mock(
            com.snapserve.notification.repository.NotificationTemplateRepository.class);
    Constructor<TemplateService> constructor =
        TemplateService.class.getDeclaredConstructor(
            com.snapserve.notification.repository.NotificationTemplateRepository.class);
    constructor.setAccessible(true);
    TemplateService templateService = constructor.newInstance(templateRepository);

    String processed =
        templateService.processTextTemplate(
            "Booking Completed - {bookingId}", Map.of("bookingId", "booking-123"));

    assertThat(processed).isEqualTo("Booking Completed - booking-123");
  }

  @Test
  void sendNotificationPassesRawHtmlTemplateToEmailStrategyForHtmlRendering() throws Exception {
    NotificationHistory history = new NotificationHistory();
    ObjectId notificationId = new ObjectId();
    ReflectionTestUtils.setField(history, "id", notificationId);

    NotificationTemplate template = new NotificationTemplate();
    ReflectionTestUtils.setField(template, "subject", "Booking Completed - {bookingId}");
    ReflectionTestUtils.setField(
        template, "bodyHtml", "<p>Hello, <span th:text=\"${customerName}\">[Name]</span></p>");

    SendNotificationRequest request = new SendNotificationRequest();
    ReflectionTestUtils.setField(request, "templateName", "BOOKING_COMPLETION");
    ReflectionTestUtils.setField(request, "channel", NotificationChannel.EMAIL);
    ReflectionTestUtils.setField(request, "recipient", "jamie@example.com");
    ReflectionTestUtils.setField(
        request, "parameters", Map.of("customerName", "Jamie", "bookingId", "booking-123"));

    when(historyService.createHistory(
            (String) ReflectionTestUtils.getField(request, "templateName"),
            (String) ReflectionTestUtils.getField(request, "recipient"),
            (NotificationChannel) ReflectionTestUtils.getField(request, "channel"),
            (Map<String, Object>) ReflectionTestUtils.getField(request, "parameters")))
        .thenReturn(history);
    when(templateService.getTemplate("BOOKING_COMPLETION", NotificationChannel.EMAIL))
        .thenReturn(template);
    when(templateService.processTextTemplate(
            "Booking Completed - {bookingId}",
            Map.of("customerName", "Jamie", "bookingId", "booking-123")))
        .thenReturn("Booking Completed - booking-123");

    notificationOrchestrator.sendNotification(request);

    verify(emailStrategy)
        .sendEmail(
            org.mockito.ArgumentMatchers.eq("jamie@example.com"),
            org.mockito.ArgumentMatchers.eq("Booking Completed - booking-123"),
            org.mockito.ArgumentMatchers.eq(
                "<p>Hello, <span th:text=\"${customerName}\">[Name]</span></p>"),
            org.mockito.ArgumentMatchers.eq(
                Map.of("customerName", "Jamie", "bookingId", "booking-123")));
    verify(historyService).markAsSent(notificationId);
  }
}
