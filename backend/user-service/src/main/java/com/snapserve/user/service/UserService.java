package com.snapserve.user.service;

import com.snapserve.common.exception.ConflictException;
import com.snapserve.common.exception.ForbiddenException;
import com.snapserve.common.exception.ResourceNotFoundException;
import com.snapserve.common.model.Role;
import com.snapserve.common.mongo.ObjectIdParser;
import com.snapserve.user.mapper.UserMapper;
import com.snapserve.user.model.UserEntity;
import com.snapserve.user.repo.UserRepository;
import com.snapserve.userclient.dto.customer.CustomerListResponse;
import com.snapserve.userclient.dto.customer.CustomerRequest;
import com.snapserve.userclient.dto.customer.CustomerResponse;
import com.snapserve.userclient.dto.specialist.SpecialistListResponse;
import com.snapserve.userclient.dto.specialist.SpecialistRequest;
import com.snapserve.userclient.dto.specialist.SpecialistResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
    user.setVerified(true);
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
    return getCustomerById(id, null, null);
  }

  public CustomerResponse getCustomerById(
      String id, String authenticatedEmail, String authenticatedRoles) {
    ObjectId objectId = parseObjectId(id, "customer");
    UserEntity customer =
        userRepository
            .findByIdAndRole(objectId, Role.CUSTOMER)
            .orElseThrow(() -> ResourceNotFoundException.of("Customer", id));

    validateCustomerReadOwnership(customer, authenticatedEmail, authenticatedRoles);
    return userMapper.toCustomerResponse(customer);
  }

  public CustomerResponse getCustomerByEmail(String email) {
    return getCustomerByEmail(email, null, null);
  }

  public CustomerResponse getCustomerByEmail(
      String email, String authenticatedEmail, String authenticatedRoles) {
    UserEntity customer =
        userRepository
            .findByEmail(email)
            .filter(user -> user.getRole() == Role.CUSTOMER)
            .orElseThrow(
                () -> new ResourceNotFoundException("Customer not found for email: " + email));

    validateCustomerReadOwnership(customer, authenticatedEmail, authenticatedRoles);
    return userMapper.toCustomerResponse(customer);
  }

  public SpecialistResponse getSpecialistById(String id) {
    return getSpecialistById(id, null, null);
  }

  public SpecialistResponse getSpecialistById(
      String id, String authenticatedEmail, String authenticatedRoles) {
    ObjectId objectId = parseObjectId(id, "specialist");
    UserEntity specialist =
        userRepository
            .findByIdAndRole(objectId, Role.SPECIALIST)
            .orElseThrow(() -> ResourceNotFoundException.of("Specialist", id));

    validateSpecialistReadOwnership(specialist, authenticatedEmail, authenticatedRoles);
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

  public CustomerResponse updateCustomer(
      String id, String authenticatedEmail, String authenticatedRoles, CustomerRequest request) {
    ObjectId objectId = parseObjectId(id, "customer");
    UserEntity customer =
        userRepository
            .findByIdAndRole(objectId, Role.CUSTOMER)
            .orElseThrow(() -> ResourceNotFoundException.of("Customer", id));

    validateCustomerOwnership(customer, authenticatedEmail, authenticatedRoles);

    if (!request.email().equalsIgnoreCase(authenticatedEmail)) {
      throw new com.snapserve.common.exception.BadRequestException(
          "Customer email must match the authenticated user.");
    }

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

  public SpecialistResponse updateSpecialist(
      String id, String authenticatedEmail, String authenticatedRoles, SpecialistRequest request) {
    ObjectId objectId = parseObjectId(id, "specialist");
    UserEntity specialist =
        userRepository
            .findByIdAndRole(objectId, Role.SPECIALIST)
            .orElseThrow(() -> ResourceNotFoundException.of("Specialist", id));

    validateSpecialistOwnership(specialist, authenticatedEmail, authenticatedRoles);

    if (!request.email().equalsIgnoreCase(authenticatedEmail)) {
      throw new com.snapserve.common.exception.BadRequestException(
          "Specialist email must match the authenticated user.");
    }

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

  public void deleteCustomer(String id, String authenticatedEmail, String authenticatedRoles) {
    ObjectId objectId = parseObjectId(id, "customer");
    UserEntity customer =
        userRepository
            .findByIdAndRole(objectId, Role.CUSTOMER)
            .orElseThrow(() -> ResourceNotFoundException.of("Customer", id));

    validateCustomerDeletionOwnership(customer, authenticatedEmail, authenticatedRoles);

    userRepository.delete(customer);
    log.info("Customer deleted: {}", customer.getEmail());
  }

  public void deleteSpecialist(String id, String authenticatedEmail, String authenticatedRoles) {
    ObjectId objectId = parseObjectId(id, "specialist");
    UserEntity specialist =
        userRepository
            .findByIdAndRole(objectId, Role.SPECIALIST)
            .orElseThrow(() -> ResourceNotFoundException.of("Specialist", id));

    validateSpecialistDeletionOwnership(specialist, authenticatedEmail, authenticatedRoles);

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

  private ObjectId parseObjectId(String id, String resourceName) {
    return ObjectIdParser.parse(id, resourceName);
  }

  private void validateCustomerOwnership(
      UserEntity customer, String authenticatedEmail, String authenticatedRoles) {
    if (!hasRole(authenticatedRoles, Role.CUSTOMER)) {
      throw new ForbiddenException("Only customers can update customer profiles.");
    }

    if (!customer.getEmail().equalsIgnoreCase(authenticatedEmail)) {
      throw new ForbiddenException("You can only update your own customer profile.");
    }
  }

  private void validateCustomerReadOwnership(
      UserEntity customer, String authenticatedEmail, String authenticatedRoles) {
    if (authenticatedEmail == null && authenticatedRoles == null) {
      return;
    }

    if (!hasRole(authenticatedRoles, Role.CUSTOMER)) {
      throw new ForbiddenException("Only customers can view customer profiles.");
    }

    if (!customer.getEmail().equalsIgnoreCase(authenticatedEmail)) {
      throw new ForbiddenException("You can only view your own customer profile.");
    }
  }

  private void validateSpecialistOwnership(
      UserEntity specialist, String authenticatedEmail, String authenticatedRoles) {
    if (!hasRole(authenticatedRoles, Role.SPECIALIST)) {
      throw new ForbiddenException("Only specialists can update specialist profiles.");
    }

    if (!specialist.getEmail().equalsIgnoreCase(authenticatedEmail)) {
      throw new ForbiddenException("You can only update your own specialist profile.");
    }
  }

  private void validateSpecialistReadOwnership(
      UserEntity specialist, String authenticatedEmail, String authenticatedRoles) {
    if (authenticatedEmail == null && authenticatedRoles == null) {
      return;
    }

    if (!hasRole(authenticatedRoles, Role.SPECIALIST)) {
      throw new ForbiddenException("Only specialists can view specialist profiles.");
    }

    if (!specialist.getEmail().equalsIgnoreCase(authenticatedEmail)) {
      throw new ForbiddenException("You can only view your own specialist profile.");
    }
  }

  private void validateCustomerDeletionOwnership(
      UserEntity customer, String authenticatedEmail, String authenticatedRoles) {
    if (!hasRole(authenticatedRoles, Role.CUSTOMER)) {
      throw new ForbiddenException("Only customers can delete customer profiles.");
    }

    if (!customer.getEmail().equalsIgnoreCase(authenticatedEmail)) {
      throw new ForbiddenException("You can only delete your own customer profile.");
    }
  }

  private void validateSpecialistDeletionOwnership(
      UserEntity specialist, String authenticatedEmail, String authenticatedRoles) {
    if (!hasRole(authenticatedRoles, Role.SPECIALIST)) {
      throw new ForbiddenException("Only specialists can delete specialist profiles.");
    }

    if (!specialist.getEmail().equalsIgnoreCase(authenticatedEmail)) {
      throw new ForbiddenException("You can only delete your own specialist profile.");
    }
  }

  private boolean hasRole(String authenticatedRoles, Role expectedRole) {
    if (authenticatedRoles == null || authenticatedRoles.isBlank()) {
      return false;
    }

    return Arrays.stream(authenticatedRoles.split(","))
        .map(String::trim)
        .filter(role -> !role.isEmpty())
        .map(role -> role.toUpperCase(Locale.ROOT))
        .anyMatch(expectedRole.name()::equals);
  }
}
