package com.ust.specialist_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.cloud.config.enabled=false"})
class SpecialistServiceApplicationTests {

  @Test
  void contextLoads() {}
}
