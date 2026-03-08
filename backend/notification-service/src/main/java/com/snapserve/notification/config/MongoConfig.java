package com.snapserve.notification.config;

import com.snapserve.notification.model.NotificationTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MongoConfig implements CommandLineRunner {

  private final MongoTemplate mongoTemplate;

  @Override
  public void run(String... args) {
    // Ensure indexes are created
    IndexOperations templateIndexOps = mongoTemplate.indexOps(NotificationTemplate.class);

    // Unique index on name field
    templateIndexOps.ensureIndex(
        new Index().on("name", org.springframework.data.domain.Sort.Direction.ASC).unique());

    log.info("MongoDB indexes verified/created");
  }
}
