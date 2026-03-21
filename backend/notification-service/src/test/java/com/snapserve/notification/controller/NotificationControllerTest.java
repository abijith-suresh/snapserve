package com.snapserve.notification.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;

import com.snapserve.common.exception.BadRequestException;
import com.snapserve.notification.service.NotificationHistoryService;
import com.snapserve.notification.service.NotificationOrchestrator;
import com.snapserve.notification.service.TemplateService;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    assertThatThrownBy(() -> notificationController.getNotificationStatus("not-an-object-id"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid notification ID format.");

    verifyNoInteractions(notificationHistoryService);
  }
}
