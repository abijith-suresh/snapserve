package com.snapserve.userservice.service;

import com.snapserve.userservice.model.Customer;
import com.snapserve.userservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements GenericUserService<Customer> {

    private CustomerRepository customerRepository;

    @Override
    public Customer createUser(Customer user) {
        return customerRepository.save(user);
    }

    @Override
    public Customer getUserById(String id) {
        return customerRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public List<Customer> getAllUsers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer updateUser(String id, Customer user) {
        user.setId(new ObjectId(id));
        return customerRepository.save(user);
    }

    @Override
    public void deleteUser(String id) {
        customerRepository.deleteById(new ObjectId(id));
    }
}
