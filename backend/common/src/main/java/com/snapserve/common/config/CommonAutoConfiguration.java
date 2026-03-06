package com.snapserve.common.config;

import com.snapserve.common.handler.GlobalExceptionHandler;
import com.snapserve.common.jwt.JwtUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@AutoConfiguration
@EnableMongoAuditing
@Import({GlobalExceptionHandler.class, JwtUtils.class})
public class CommonAutoConfiguration {}
