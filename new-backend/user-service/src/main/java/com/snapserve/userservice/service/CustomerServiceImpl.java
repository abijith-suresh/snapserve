package com.snapserve.userservice.service;

import com.snapserve.userservice.dto.CustomerRequest;
import com.snapserve.userservice.dto.CustomerResponse;
import com.snapserve.userservice.exception.ResourceNotFoundException;
import com.snapserve.userservice.mapper.CustomerMapper;
import com.snapserve.userservice.model.Customer;
import com.snapserve.userservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements GenericUserService<Customer, CustomerRequest, CustomerResponse> {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponse createUser(CustomerRequest request) {
        Customer customer = CustomerMapper.toEntity(request);
        Customer saved = customerRepository.save(customer);
        return CustomerMapper.toResponse(saved);
    }

    @Override
    public CustomerResponse getUserById(String id) {
        Customer customer = customerRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        return CustomerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllUsers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse updateUser(String id, CustomerRequest request) {
        Customer existing = customerRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));

        Customer updated = CustomerMapper.toEntity(request);
        updated.setId(existing.getId());

        Customer saved = customerRepository.save(updated);
        return CustomerMapper.toResponse(saved);
    }

    @Override
    public void deleteUser(String id) {
        Customer customer = customerRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        customerRepository.delete(customer);
    }
}

