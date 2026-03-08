package com.snapserve.notification.model;

import com.snapserve.common.model.Auditable;
import com.snapserve.notificationclient.constants.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notification_templates")
public class NotificationTemplate extends Auditable {

  @Id private ObjectId id;

  @Indexed(unique = true)
  private String name;

  private NotificationChannel channel;

  private String subject;

  private String bodyHtml;

  private String bodyText;

  private Integer version;

  private Boolean active;
}
