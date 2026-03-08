package com.snapserve.notification.repository;

import com.snapserve.notification.model.NotificationHistory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationHistoryRepository
    extends MongoRepository<NotificationHistory, ObjectId> {}
