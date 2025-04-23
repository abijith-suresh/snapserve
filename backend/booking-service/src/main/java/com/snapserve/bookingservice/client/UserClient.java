package com.snapserve.bookingservice.client;

import com.snapserve.bookingservice.dto.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserInfoResponse getUserInfoById(@PathVariable("id") String id);
}
