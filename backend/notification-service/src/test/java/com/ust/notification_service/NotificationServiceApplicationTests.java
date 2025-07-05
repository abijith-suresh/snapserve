package com.ust.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(NotificationServiceTestConfig.class)
class NotificationServiceApplicationTests {

  @Test
  void contextLoads() {}
}
