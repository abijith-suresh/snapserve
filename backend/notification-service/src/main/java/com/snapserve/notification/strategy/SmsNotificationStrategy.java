package com.snapserve.notification.strategy;

import com.snapserve.notificationclient.constants.NotificationChannel;
import com.snapserve.notificationclient.request.SendNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsNotificationStrategy implements NotificationChannelStrategy {

  @Override
  public boolean supports(NotificationChannel channel) {
    return channel == NotificationChannel.SMS;
  }

  @Override
  public void send(SendNotificationRequest request) {
    // TODO: Implement SMS sending using Twilio or AWS SNS
    // This is a placeholder for future implementation
    log.warn("SMS notification not yet implemented. Recipient: {}", request.getRecipient());
    throw new UnsupportedOperationException("SMS notifications are not yet implemented");
  }
}
