package com.snapserve.notification.strategy;

import com.snapserve.notificationclient.constants.NotificationChannel;
import com.snapserve.notificationclient.request.SendNotificationRequest;

public interface NotificationChannelStrategy {

  boolean supports(NotificationChannel channel);

  void send(SendNotificationRequest request) throws Exception;
}
