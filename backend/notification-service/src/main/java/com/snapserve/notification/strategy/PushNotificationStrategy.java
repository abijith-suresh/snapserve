package com.snapserve.notification.strategy;

import com.snapserve.notificationclient.constants.NotificationChannel;
import com.snapserve.notificationclient.request.SendNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushNotificationStrategy implements NotificationChannelStrategy {

  @Override
  public boolean supports(NotificationChannel channel) {
    return channel == NotificationChannel.PUSH;
  }

  @Override
  public void send(SendNotificationRequest request) {
    // TODO: Implement push notifications using Firebase Cloud Messaging (FCM) or APNs
    // This is a placeholder for future implementation
    log.warn("Push notification not yet implemented. Recipient: {}", request.getRecipient());
    throw new UnsupportedOperationException("Push notifications are not yet implemented");
  }
}
