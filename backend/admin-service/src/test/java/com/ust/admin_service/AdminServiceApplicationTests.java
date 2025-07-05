package com.ust.admin_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.cloud.config.enabled=false", "spring.cloud.discovery.enabled=false"})
class AdminServiceApplicationTests {

  @Test
  void contextLoads() {}
}
