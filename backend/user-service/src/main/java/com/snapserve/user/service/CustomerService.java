package com.snapserve.user.service;

import com.snapserve.user.model.Customer;
import com.snapserve.user.repo.CustomerRepository;
import com.snapserve.userclient.dto.CustomerDto;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

  @Autowired private CustomerRepository customerRepository;

  private CustomerDto modelToDto(Customer customer) {
    CustomerDto dto = new CustomerDto();
    dto.setId(customer.getId().toString());
    dto.setName(customer.getName());
    dto.setEmail(customer.getEmail());
    dto.setPhone(customer.getPhone());
    return dto;
  }

  private void dtoToModel(Customer customer, CustomerDto dto) {
    customer.setName(dto.getName());
    customer.setEmail(dto.getEmail());
    customer.setPhone(dto.getPhone());
  }

  public List<CustomerDto> findAllCustomers() {
    return customerRepository.findAll().stream().map(this::modelToDto).collect(Collectors.toList());
  }

  public CustomerDto findCustomerById(ObjectId id) {
    return customerRepository.findById(id).map(this::modelToDto).orElse(null);
  }

  public Customer createCustomer(CustomerDto dto) {
    Customer customer = new Customer();
    dtoToModel(customer, dto);
    return customerRepository.save(customer);
  }

  public Customer updateCustomer(ObjectId id, Customer details) {
    details.setId(id);
    return customerRepository.save(details);
  }

  public void deleteCustomerById(ObjectId id) {
    customerRepository.deleteById(id);
  }
}
