package com.snapserve.booking.service;

import com.snapserve.booking.model.Booking;
import com.snapserve.notificationclient.client.NotificationServiceClient;
import com.snapserve.notificationclient.constants.NotificationChannel;
import com.snapserve.notificationclient.constants.NotificationTemplateNames;
import com.snapserve.notificationclient.request.SendNotificationRequest;
import com.snapserve.userclient.dto.customer.CustomerResponse;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingNotificationDispatcher {

  private static final DateTimeFormatter APPOINTMENT_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private final NotificationServiceClient notificationServiceClient;

  public void sendBookingCreatedConfirmation(Booking booking, CustomerResponse customer) {
    notificationServiceClient.sendNotification(
        SendNotificationRequest.builder()
            .templateName(NotificationTemplateNames.BOOKING_CONFIRMATION)
            .channel(NotificationChannel.EMAIL)
            .recipient(customer.email())
            .parameters(
                Map.of(
                    "customerName", customer.name(),
                    "bookingId", booking.getId().toString(),
                    "appointmentTime", booking.getBookingDate().format(APPOINTMENT_TIME_FORMATTER)))
            .build());
  }
}
