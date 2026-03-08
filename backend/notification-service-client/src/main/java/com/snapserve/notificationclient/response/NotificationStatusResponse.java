package com.snapserve.notificationclient.response;

import com.snapserve.notificationclient.constants.NotificationChannel;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationStatusResponse {

  private String notificationId;
  private String templateName;
  private String recipient;
  private NotificationChannel channel;
  private String status;
  private Map<String, Object> parameters;
  private Instant sentAt;
  private Integer retryCount;
  private String errorMessage;
  private Instant createdAt;
}
