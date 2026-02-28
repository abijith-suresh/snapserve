package com.snapserve.user.service;

import com.snapserve.user.dto.CustomerDto;
import com.snapserve.user.dto.EmailUpdateDto;
import com.snapserve.user.model.Customer;
import com.snapserve.user.repo.CustomerRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CustomerService {

  @Autowired private CustomerRepository customerRepository;

  @Value("${notification.service.url}")
  private String notificationServiceUrl;

  private CustomerDto modelToDto(Customer customer) {
    CustomerDto dto = new CustomerDto();
    dto.setId(customer.getId().toString());
    dto.setName(customer.getName());
    dto.setEmail(customer.getEmail());
    dto.setPhone(customer.getPhone());
    dto.setGender(customer.getGender());
    dto.setDob(customer.getDob());
    dto.setAddress(customer.getAddress());
    dto.setProfilePictureUrl(customer.getProfilePictureUrl());
    return dto;
  }

  private void dtoToModel(Customer customer, CustomerDto dto) {
    customer.setName(dto.getName());
    customer.setEmail(dto.getEmail());
    customer.setPhone(dto.getPhone());
    customer.setGender(dto.getGender());
    customer.setDob(dto.getDob());
    customer.setAddress(dto.getAddress());
    customer.setProfilePictureUrl(dto.getProfilePictureUrl());
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
    Customer saved = customerRepository.save(customer);
    sendRegistrationEmail(saved.getEmail(), saved.getName());
    return saved;
  }

  public Customer updateCustomer(ObjectId id, Customer details) {
    details.setId(id);
    return customerRepository.save(details);
  }

  public void deleteCustomerById(ObjectId id) {
    customerRepository.deleteById(id);
  }

  public CustomerDto findByEmail(String email) {
    return customerRepository.findByEmail(email).map(this::modelToDto).orElse(null);
  }

  public Customer updateCustomerEmail(ObjectId id, EmailUpdateDto dto) {
    return customerRepository
        .findById(id)
        .map(
            existing -> {
              existing.setEmail(dto.getEmail());
              return customerRepository.save(existing);
            })
        .orElse(null);
  }

  public void deleteCustomerByEmail(String email) {
    customerRepository.findByEmail(email).ifPresent(customerRepository::delete);
  }

  private void sendRegistrationEmail(String to, String name) {
    try {
      RestClient.create()
          .post()
          .uri(
              notificationServiceUrl
                  + "/api/notifications/send-registration-success?to={to}&name={name}",
              to,
              name)
          .retrieve()
          .toBodilessEntity();
    } catch (RestClientException e) {
      System.out.println("Failed to send registration email: " + e.getMessage());
    }
  }
}
