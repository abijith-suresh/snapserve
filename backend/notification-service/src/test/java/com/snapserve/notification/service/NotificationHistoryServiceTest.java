package com.snapserve.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snapserve.notification.model.NotificationHistory;
import com.snapserve.notification.model.NotificationStatus;
import com.snapserve.notification.repository.NotificationHistoryRepository;
import com.snapserve.notificationclient.constants.NotificationChannel;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationHistoryServiceTest {

  @Mock private NotificationHistoryRepository historyRepository;

  private NotificationHistoryService notificationHistoryService;

  @BeforeEach
  void setUp() {
    notificationHistoryService = new NotificationHistoryService(historyRepository);
  }

  @Test
  void createHistoryStoresPendingEnumStatus() {
    when(historyRepository.save(any(NotificationHistory.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    notificationHistoryService.createHistory(
        "booking-created",
        "jamie@example.com",
        NotificationChannel.EMAIL,
        Map.of("bookingId", "1"));

    ArgumentCaptor<NotificationHistory> historyCaptor =
        ArgumentCaptor.forClass(NotificationHistory.class);
    verify(historyRepository).save(historyCaptor.capture());
    assertThat(historyCaptor.getValue().getStatus()).isEqualTo(NotificationStatus.PENDING);
  }
}
