package com.snapserve.userservice.controller;

import com.snapserve.userservice.model.Customer;
import com.snapserve.userservice.service.GenericUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private GenericUserService<Customer> customerService;

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return customerService.createUser(customer);
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable String id) {
        return customerService.getUserById(id);
    }

    @GetMapping
    public List<Customer> getAll() {
        return customerService.getAllUsers();
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable String id, @RequestBody Customer customer) {
        return customerService.updateUser(id, customer);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        customerService.deleteUser(id);
    }
}
