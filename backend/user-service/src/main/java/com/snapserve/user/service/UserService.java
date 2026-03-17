package com.snapserve.user.service;

import com.snapserve.common.exception.ConflictException;
import com.snapserve.common.exception.ResourceNotFoundException;
import com.snapserve.common.model.Role;
import com.snapserve.user.mapper.UserMapper;
import com.snapserve.user.model.UserEntity;
import com.snapserve.user.repo.UserRepository;
import com.snapserve.userclient.dto.customer.CustomerListResponse;
import com.snapserve.userclient.dto.customer.CustomerRequest;
import com.snapserve.userclient.dto.customer.CustomerResponse;
import com.snapserve.userclient.dto.specialist.SpecialistListResponse;
import com.snapserve.userclient.dto.specialist.SpecialistRequest;
import com.snapserve.userclient.dto.specialist.SpecialistResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public CustomerResponse createCustomer(CustomerRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new ConflictException("User with email " + request.email() + " already exists");
    }

    UserEntity user = userMapper.toCustomerEntity(request);
    user.setRole(Role.CUSTOMER);
    user.setVerified(true);
    user = userRepository.save(user);

    log.info("Customer created with email: {}", request.email());
    return userMapper.toCustomerResponse(user);
  }

  public SpecialistResponse createSpecialist(SpecialistRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new ConflictException("User with email " + request.email() + " already exists");
    }

    UserEntity user = userMapper.toSpecialistEntity(request);
    user.setRole(Role.SPECIALIST);
    user.setVerified(false);
    user.setHourlyRate(request.hourlyRate());
    user = userRepository.save(user);

    log.info("Specialist created with email: {}", request.email());
    return userMapper.toSpecialistResponse(user);
  }

  public List<CustomerResponse> getAllCustomers() {
    List<UserEntity> customers = userRepository.findByRole(Role.CUSTOMER);
    log.debug("Found {} customers", customers.size());
    return userMapper.toCustomerResponseList(customers);
  }

  public CustomerListResponse getCustomers(Pageable pageable) {
    log.debug("Fetching customers with pagination: {}", pageable);
    Page<UserEntity> customerPage = userRepository.findByRole(Role.CUSTOMER, pageable);
    log.debug("Found {} customers", customerPage.getTotalElements());
    return toCustomerListResponse(customerPage);
  }

  public List<SpecialistResponse> getAllSpecialists() {
    List<UserEntity> specialists = userRepository.findByRole(Role.SPECIALIST);
    log.debug("Found {} specialists", specialists.size());
    return userMapper.toSpecialistResponseList(specialists);
  }

  public SpecialistListResponse getSpecialists(Pageable pageable) {
    log.debug("Fetching specialists with pagination: {}", pageable);
    Page<UserEntity> specialistPage = userRepository.findByRole(Role.SPECIALIST, pageable);
    log.debug("Found {} specialists", specialistPage.getTotalElements());
    return toSpecialistListResponse(specialistPage);
  }

  public CustomerResponse getCustomerById(String id) {
    ObjectId objectId = new ObjectId(id);
    UserEntity customer =
        userRepository
            .findByIdAndRole(objectId, Role.CUSTOMER)
            .orElseThrow(() -> ResourceNotFoundException.of("Customer", id));
    return userMapper.toCustomerResponse(customer);
  }

  public SpecialistResponse getSpecialistById(String id) {
    ObjectId objectId = new ObjectId(id);
    UserEntity specialist =
        userRepository
            .findByIdAndRole(objectId, Role.SPECIALIST)
            .orElseThrow(() -> ResourceNotFoundException.of("Specialist", id));
    return userMapper.toSpecialistResponse(specialist);
  }

  public List<SpecialistResponse> getSpecialistsByService(String service) {
    List<UserEntity> specialists =
        userRepository.findByRoleAndServicesContaining(Role.SPECIALIST, service);
    log.debug("Found {} specialists with service: {}", specialists.size(), service);
    return userMapper.toSpecialistResponseList(specialists);
  }

  public SpecialistListResponse getSpecialistsByService(String service, Pageable pageable) {
    log.debug("Fetching specialists by service: {} with pagination: {}", service, pageable);
    Page<UserEntity> specialistPage =
        userRepository.findByRoleAndServicesContaining(Role.SPECIALIST, service, pageable);
    log.debug("Found {} specialists with service: {}", specialistPage.getTotalElements(), service);
    return toSpecialistListResponse(specialistPage);
  }

  public CustomerResponse updateCustomer(String id, CustomerRequest request) {
    ObjectId objectId = new ObjectId(id);
    UserEntity customer =
        userRepository
            .findByIdAndRole(objectId, Role.CUSTOMER)
            .orElseThrow(() -> ResourceNotFoundException.of("Customer", id));

    if (!customer.getEmail().equals(request.email())
        && userRepository.existsByEmail(request.email())) {
      throw new ConflictException("User with email " + request.email() + " already exists");
    }

    customer.setEmail(request.email());
    customer.setName(request.name());
    customer.setPhone(request.phone());
    customer.setAddress(request.address());
    customer.setPreferredPaymentMethod(request.preferredPaymentMethod());

    customer = userRepository.save(customer);
    log.info("Customer updated: {}", customer.getEmail());
    return userMapper.toCustomerResponse(customer);
  }

  public SpecialistResponse updateSpecialist(String id, SpecialistRequest request) {
    ObjectId objectId = new ObjectId(id);
    UserEntity specialist =
        userRepository
            .findByIdAndRole(objectId, Role.SPECIALIST)
            .orElseThrow(() -> ResourceNotFoundException.of("Specialist", id));

    if (!specialist.getEmail().equals(request.email())
        && userRepository.existsByEmail(request.email())) {
      throw new ConflictException("User with email " + request.email() + " already exists");
    }

    specialist.setEmail(request.email());
    specialist.setName(request.name());
    specialist.setPhone(request.phone());
    specialist.setTitle(request.title());
    specialist.setServices(request.services());
    specialist.setHourlyRate(request.hourlyRate());

    specialist = userRepository.save(specialist);
    log.info("Specialist updated: {}", specialist.getEmail());
    return userMapper.toSpecialistResponse(specialist);
  }

  public void deleteCustomer(String id) {
    ObjectId objectId = new ObjectId(id);
    UserEntity customer =
        userRepository
            .findByIdAndRole(objectId, Role.CUSTOMER)
            .orElseThrow(() -> ResourceNotFoundException.of("Customer", id));

    userRepository.delete(customer);
    log.info("Customer deleted: {}", customer.getEmail());
  }

  public void deleteSpecialist(String id) {
    ObjectId objectId = new ObjectId(id);
    UserEntity specialist =
        userRepository
            .findByIdAndRole(objectId, Role.SPECIALIST)
            .orElseThrow(() -> ResourceNotFoundException.of("Specialist", id));

    userRepository.delete(specialist);
    log.info("Specialist deleted: {}", specialist.getEmail());
  }

  private CustomerListResponse toCustomerListResponse(Page<UserEntity> page) {
    return new CustomerListResponse(
        userMapper.toCustomerResponseList(page.getContent()),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }

  private SpecialistListResponse toSpecialistListResponse(Page<UserEntity> page) {
    return new SpecialistListResponse(
        userMapper.toSpecialistResponseList(page.getContent()),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }
}
