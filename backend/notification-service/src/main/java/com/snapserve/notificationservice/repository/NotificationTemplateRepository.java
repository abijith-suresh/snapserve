package com.snapserve.notificationservice.repository;

import com.snapserve.notificationservice.model.NotificationTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends MongoRepository<NotificationTemplate, String> {
    Optional<NotificationTemplate> findByName(String name);
}
