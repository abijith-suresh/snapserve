package com.snapserve.userservice.repository;

import com.snapserve.userservice.model.Customer;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends UserRepository<Customer> {
}
