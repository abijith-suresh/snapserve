package com.snapserve.notification.model;

import com.snapserve.common.model.Auditable;
import com.snapserve.notificationclient.constants.NotificationChannel;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notification_history")
public class NotificationHistory extends Auditable {

  @Id private ObjectId id;

  private String templateName;

  private String recipient;

  private NotificationChannel channel;

  private String status;

  private Map<String, Object> parameters;

  private Instant sentAt;

  private Integer retryCount;

  private String errorMessage;
}
