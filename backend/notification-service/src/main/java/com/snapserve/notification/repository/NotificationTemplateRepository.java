package com.snapserve.notification.repository;

import com.snapserve.notification.model.NotificationTemplate;
import com.snapserve.notificationclient.constants.NotificationChannel;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTemplateRepository
    extends MongoRepository<NotificationTemplate, ObjectId> {

  Optional<NotificationTemplate> findByNameAndChannelAndActiveTrue(
      String name, NotificationChannel channel);

  Optional<NotificationTemplate> findByName(String name);
}
