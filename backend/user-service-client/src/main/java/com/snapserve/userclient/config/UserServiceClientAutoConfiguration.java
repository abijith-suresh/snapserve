package com.snapserve.userclient.config;

import com.snapserve.userclient.client.UserServiceClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

@AutoConfiguration
@ConditionalOnClass(FeignClient.class)
@EnableFeignClients(clients = {UserServiceClient.class})
public class UserServiceClientAutoConfiguration {}
