package com.snapserve.userservice.service;

import com.snapserve.userservice.dto.request.CustomerRequest;
import com.snapserve.userservice.dto.response.CustomerResponse;
import com.snapserve.userservice.dto.response.PagedResponse;
import com.snapserve.userservice.exception.ResourceNotFoundException;
import com.snapserve.userservice.mapper.CustomerMapper;
import com.snapserve.userservice.model.Customer;
import com.snapserve.userservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public PagedResponse<CustomerResponse> getAllUsers(Pageable pageable) {
        Page<Customer> page = customerRepository.findAll(pageable);

        List<CustomerResponse> customerResponses = page.getContent()
                .stream()
                .map(CustomerMapper::toResponse)
                .toList();

        return PagedResponse.<CustomerResponse>builder()
                .content(customerResponses)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
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

