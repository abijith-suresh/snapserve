package com.snapserve.common.config;

import com.snapserve.common.handler.GlobalExceptionHandler;
import com.snapserve.common.jwt.JwtUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({GlobalExceptionHandler.class, JwtUtils.class})
public class CommonAutoConfiguration {

  @Configuration
  @ConditionalOnClass(name = "org.springframework.data.mongodb.config.EnableMongoAuditing")
  @org.springframework.data.mongodb.config.EnableMongoAuditing
  static class MongoAuditingConfiguration {}
}
