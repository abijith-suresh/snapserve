package com.snapserve.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.snapserve.booking.model.Booking;
import com.snapserve.notificationclient.client.NotificationServiceClient;
import com.snapserve.notificationclient.constants.NotificationChannel;
import com.snapserve.notificationclient.constants.NotificationTemplateNames;
import com.snapserve.notificationclient.request.SendNotificationRequest;
import com.snapserve.userclient.dto.customer.CustomerResponse;
import java.time.LocalDateTime;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookingNotificationDispatcherTest {

  @Mock private NotificationServiceClient notificationServiceClient;

  @Test
  void sendBookingCreatedConfirmationBuildsBookingConfirmationEmailRequest() {
    Booking booking = new Booking();
    ReflectionTestUtils.setField(booking, "id", new ObjectId("6613f8d69f9d5b42e88f1201"));
    ReflectionTestUtils.setField(booking, "bookingDate", LocalDateTime.of(2026, 4, 1, 10, 0));
    CustomerResponse customer =
        new CustomerResponse(
            "customer-1", "jamie@example.com", "Jamie", "555-0100", "Main St", "CARD", null, null);

    BookingNotificationDispatcher dispatcher =
        new BookingNotificationDispatcher(notificationServiceClient);

    dispatcher.sendBookingCreatedConfirmation(booking, customer);

    ArgumentCaptor<SendNotificationRequest> requestCaptor =
        ArgumentCaptor.forClass(SendNotificationRequest.class);
    verify(notificationServiceClient).sendNotification(requestCaptor.capture());

    SendNotificationRequest request = requestCaptor.getValue();
    assertThat(request.getTemplateName()).isEqualTo(NotificationTemplateNames.BOOKING_CONFIRMATION);
    assertThat(request.getChannel()).isEqualTo(NotificationChannel.EMAIL);
    assertThat(request.getRecipient()).isEqualTo("jamie@example.com");
    assertThat(request.getParameters())
        .containsEntry("customerName", "Jamie")
        .containsEntry("bookingId", "6613f8d69f9d5b42e88f1201")
        .containsEntry("appointmentTime", "2026-04-01 10:00");
  }
}
