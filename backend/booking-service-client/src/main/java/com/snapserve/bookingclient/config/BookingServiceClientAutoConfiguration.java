package com.snapserve.bookingclient.config;

import com.snapserve.bookingclient.client.BookingServiceClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

@AutoConfiguration
@ConditionalOnClass(FeignClient.class)
@EnableFeignClients(clients = {BookingServiceClient.class})
public class BookingServiceClientAutoConfiguration {}
