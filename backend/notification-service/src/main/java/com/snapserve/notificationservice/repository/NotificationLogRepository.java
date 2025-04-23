package com.snapserve.notificationservice.repository;

import com.snapserve.notificationservice.model.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
}
