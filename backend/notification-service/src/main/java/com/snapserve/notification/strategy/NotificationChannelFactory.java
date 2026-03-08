package com.snapserve.notification.strategy;

import com.snapserve.notification.exception.InvalidChannelException;
import com.snapserve.notificationclient.constants.NotificationChannel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationChannelFactory {

  private final List<NotificationChannelStrategy> strategies;

  public NotificationChannelStrategy getStrategy(NotificationChannel channel) {
    return strategies.stream()
        .filter(strategy -> strategy.supports(channel))
        .findFirst()
        .orElseThrow(() -> new InvalidChannelException(channel.name()));
  }
}
