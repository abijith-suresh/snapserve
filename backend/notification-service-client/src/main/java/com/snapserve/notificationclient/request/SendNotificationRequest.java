package com.snapserve.notificationclient.request;

import com.snapserve.notificationclient.constants.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {

  @NotBlank(message = "Template name is required")
  private String templateName;

  @NotNull(message = "Channel is required")
  private NotificationChannel channel;

  @NotBlank(message = "Recipient is required")
  private String recipient;

  private Map<String, Object> parameters;
}
