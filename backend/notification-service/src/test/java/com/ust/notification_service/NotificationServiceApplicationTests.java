package com.ust.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.cloud.config.enabled=false"})
class NotificationServiceApplicationTests {

  @Test
  void contextLoads() {}
}
