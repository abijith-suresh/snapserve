package com.snapserve.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notification_templates")
public class NotificationTemplate {
    @Id
    private String id;

    private String name;
    private String subject;
    private String content;
    private boolean active;
}

