package com.ust.complaint_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.cloud.config.enabled=false"})
class ComplaintServiceApplicationTests {

  @Test
  void contextLoads() {}
}
