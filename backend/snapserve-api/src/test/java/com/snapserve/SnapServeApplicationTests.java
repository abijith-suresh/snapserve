package com.snapserve;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SnapServeApplicationTests {

  @Test
  void contextLoads(ApplicationContext applicationContext) {
    assertNotNull(applicationContext.getBean(SnapServeApplication.class));
  }
}
