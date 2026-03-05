package com.snapserve.user.controller;

import com.snapserve.user.model.Customer;
import com.snapserve.user.service.CustomerService;
import com.snapserve.userclient.dto.CustomerDto;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

  @Autowired private CustomerService customerService;

  @GetMapping("/")
  public ResponseEntity<List<CustomerDto>> getAllCustomers() {
    return ResponseEntity.ok(customerService.findAllCustomers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerDto> getCustomerById(@PathVariable String id) {
    CustomerDto customer = customerService.findCustomerById(new ObjectId(id));
    if (customer == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(customer);
  }

  @PostMapping("/")
  public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDto customerDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(customerService.createCustomer(customerDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Customer> updateCustomer(
      @PathVariable String id, @RequestBody Customer customerDetails) {
    Customer updated = customerService.updateCustomer(new ObjectId(id), customerDetails);
    if (updated == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCustomerById(@PathVariable String id) {
    customerService.deleteCustomerById(new ObjectId(id));
    return ResponseEntity.noContent().build();
  }
}
