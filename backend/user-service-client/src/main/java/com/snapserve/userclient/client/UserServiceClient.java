package com.snapserve.userclient.client;

import com.snapserve.userclient.dto.customer.CustomerResponse;
import com.snapserve.userclient.dto.specialist.SpecialistResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {

  @GetMapping("/api/v1/customers/{id}")
  CustomerResponse getCustomerById(@PathVariable("id") String id);

  @GetMapping("/api/v1/specialists/{id}")
  SpecialistResponse getSpecialistById(@PathVariable("id") String id);
}
