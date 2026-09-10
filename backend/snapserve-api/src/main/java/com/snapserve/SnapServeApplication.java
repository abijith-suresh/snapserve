package com.snapserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SnapServeApplication {

  public static void main(String[] args) {
    SpringApplication.run(SnapServeApplication.class, args);
  }
}
