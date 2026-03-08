package com.snapserve.notificationclient.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationResponse {

  private String notificationId;
  private String status;
  private String message;
}
