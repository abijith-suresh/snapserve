package com.snapserve.notificationclient.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.snapserve.notificationclient")
public class NotificationServiceClientAutoConfiguration {}
