package com.snapserve.notificationclient.response;

import com.snapserve.notificationclient.constants.NotificationChannel;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateResponse {

  private String id;
  private String name;
  private NotificationChannel channel;
  private String subject;
  private String version;
  private Boolean active;
  private Instant createdAt;
  private Instant updatedAt;
}
