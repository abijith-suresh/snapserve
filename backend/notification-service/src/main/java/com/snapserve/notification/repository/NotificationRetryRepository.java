package com.snapserve.notification.repository;

import com.snapserve.notification.model.NotificationRetry;
import java.time.Instant;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRetryRepository extends MongoRepository<NotificationRetry, ObjectId> {

  List<NotificationRetry> findByNextRetryAtLessThanEqual(Instant now);
}
