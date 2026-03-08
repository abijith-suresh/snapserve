package com.snapserve.notification.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notification_retries")
public class NotificationRetry {

  @Id private ObjectId id;

  private ObjectId notificationId;

  private Instant nextRetryAt;

  private Integer retryAttempt;

  private Integer maxRetries;
}
