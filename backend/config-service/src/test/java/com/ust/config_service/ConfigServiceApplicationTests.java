package com.ust.config_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.cloud.config.enabled=false", "spring.cloud.discovery.enabled=false"})
class ConfigServiceApplicationTests {

  @Test
  void contextLoads() {}
}
