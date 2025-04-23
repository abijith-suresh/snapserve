package com.snapserve.authservice.client;

import com.snapserve.authservice.client.dto.AdminCreateRequest;
import com.snapserve.authservice.client.dto.CustomerCreateRequest;
import com.snapserve.authservice.client.dto.SpecialistCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/admins")
    void createAdmin(@RequestBody AdminCreateRequest request);

    @PostMapping("/customers")
    void createCustomer(@RequestBody CustomerCreateRequest request);

    @PostMapping("/specialists")
    void createSpecialist(@RequestBody SpecialistCreateRequest request);
}