package com.snapserve.user.service;

import com.snapserve.common.exception.ConflictException;
import com.snapserve.common.exception.ResourceNotFoundException;
import com.snapserve.common.model.Role;
import com.snapserve.user.dto.customer.CustomerRequest;
import com.snapserve.user.dto.customer.CustomerResponse;
import com.snapserve.user.dto.specialist.SpecialistRequest;
import com.snapserve.user.dto.specialist.SpecialistResponse;
import com.snapserve.user.mapper.UserMapper;
import com.snapserve.user.model.UserEntity;
import com.snapserve.user.repo.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
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
    user = userRepository.save(user);

    log.info("Specialist created with email: {}", request.email());
    return userMapper.toSpecialistResponse(user);
  }

  public List<CustomerResponse> getAllCustomers() {
    List<UserEntity> customers = userRepository.findByRole(Role.CUSTOMER);
    log.debug("Found {} customers", customers.size());
    return userMapper.toCustomerResponseList(customers);
  }

  public List<SpecialistResponse> getAllSpecialists() {
    List<UserEntity> specialists = userRepository.findByRole(Role.SPECIALIST);
    log.debug("Found {} specialists", specialists.size());
    return userMapper.toSpecialistResponseList(specialists);
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
}
