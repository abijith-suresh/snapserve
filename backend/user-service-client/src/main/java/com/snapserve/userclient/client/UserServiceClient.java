package com.snapserve.userclient.client;

import com.snapserve.userclient.dto.CustomerDto;
import com.snapserve.userclient.dto.SpecialistDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {

  @GetMapping("/api/v1/customers/{id}")
  CustomerDto getCustomerById(@PathVariable("id") String id);

  @GetMapping("/api/v1/specialists/{id}")
  SpecialistDto getSpecialistById(@PathVariable("id") String id);
}
