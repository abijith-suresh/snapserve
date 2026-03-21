package com.snapserve.notification.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.snapserve.common.exception.BadRequestException;
import com.snapserve.common.exception.ForbiddenException;
import com.snapserve.notification.model.NotificationStatus;
import com.snapserve.notification.service.NotificationHistoryService;
import com.snapserve.notification.service.NotificationOrchestrator;
import com.snapserve.notification.service.TemplateService;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

  @Mock private NotificationOrchestrator notificationOrchestrator;
  @Mock private NotificationHistoryService notificationHistoryService;
  @Mock private TemplateService templateService;

  private NotificationController notificationController;

  @BeforeEach
  void setUp() throws Exception {
    Constructor<NotificationController> constructor =
        NotificationController.class.getDeclaredConstructor(
            NotificationOrchestrator.class,
            NotificationHistoryService.class,
            TemplateService.class);
    constructor.setAccessible(true);
    notificationController =
        constructor.newInstance(
            notificationOrchestrator, notificationHistoryService, templateService);
  }

  @Test
  void getNotificationStatusRejectsMalformedIdBeforeServiceAccess() {
    assertThatThrownBy(
            () ->
                notificationController.getNotificationStatus(
                    "not-an-object-id", "customer@snapserve.com", "CUSTOMER"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid notification ID format.");

    verifyNoInteractions(notificationHistoryService);
  }

  @Test
  void getNotificationStatusRejectsNonRecipientAccess() {
    org.bson.types.ObjectId notificationId = new org.bson.types.ObjectId();
    com.snapserve.notification.model.NotificationHistory history =
        new com.snapserve.notification.model.NotificationHistory();
    ReflectionTestUtils.setField(history, "id", notificationId);
    ReflectionTestUtils.setField(history, "recipient", "owner@snapserve.com");

    org.mockito.Mockito.when(notificationHistoryService.getHistory(notificationId))
        .thenReturn(history);

    assertThatThrownBy(
            () ->
                notificationController.getNotificationStatus(
                    notificationId.toString(), "other@snapserve.com", "CUSTOMER"))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("You can only access your own notification history.");
  }

  @Test
  void getNotificationStatusAllowsRecipientAccess() {
    org.bson.types.ObjectId notificationId = new org.bson.types.ObjectId();
    com.snapserve.notification.model.NotificationHistory history =
        new com.snapserve.notification.model.NotificationHistory();
    ReflectionTestUtils.setField(history, "id", notificationId);
    ReflectionTestUtils.setField(history, "recipient", "owner@snapserve.com");
    ReflectionTestUtils.setField(history, "templateName", "booking-created");
    ReflectionTestUtils.setField(history, "status", NotificationStatus.SENT);

    org.mockito.Mockito.when(notificationHistoryService.getHistory(notificationId))
        .thenReturn(history);

    notificationController.getNotificationStatus(
        notificationId.toString(), "owner@snapserve.com", "CUSTOMER");

    verify(notificationHistoryService).getHistory(notificationId);
  }

  @Test
  void templateManagementRejectsNonSpecialists() {
    assertThatThrownBy(
            () -> notificationController.getTemplates("customer@snapserve.com", "CUSTOMER"))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Only specialists can manage notification templates.");

    verifyNoInteractions(templateService);
  }
}
