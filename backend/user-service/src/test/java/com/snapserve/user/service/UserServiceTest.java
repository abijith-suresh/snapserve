package com.snapserve.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.snapserve.common.exception.BadRequestException;
import com.snapserve.common.exception.ConflictException;
import com.snapserve.common.model.Role;
import com.snapserve.user.mapper.UserMapper;
import com.snapserve.user.model.UserEntity;
import com.snapserve.user.repo.UserRepository;
import com.snapserve.userclient.dto.customer.CustomerRequest;
import com.snapserve.userclient.dto.customer.CustomerResponse;
import com.snapserve.userclient.dto.specialist.SpecialistRequest;
import com.snapserve.userclient.dto.specialist.SpecialistResponse;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  private UserService userService;

  @BeforeEach
  void setUp() throws Exception {
    UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    Constructor<UserService> constructor =
        UserService.class.getDeclaredConstructor(UserRepository.class, UserMapper.class);
    constructor.setAccessible(true);
    userService = constructor.newInstance(userRepository, userMapper);
  }

  @Test
  void createCustomerMarksCustomersAsVerified() {
    CustomerRequest request =
        new CustomerRequest(
            "customer@example.com", "Jamie Customer", "+15555550101", "123 Main St", "PAYPAL");

    when(userRepository.existsByEmail(request.email())).thenReturn(false);
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(
            invocation -> {
              UserEntity saved = invocation.getArgument(0);
              ReflectionTestUtils.setField(saved, "id", new ObjectId());
              return saved;
            });

    CustomerResponse response = userService.createCustomer(request);

    ArgumentCaptor<UserEntity> savedUser = ArgumentCaptor.forClass(UserEntity.class);
    verify(userRepository).save(savedUser.capture());

    assertThat(ReflectionTestUtils.getField(savedUser.getValue(), "role")).isEqualTo(Role.CUSTOMER);
    assertThat(ReflectionTestUtils.getField(savedUser.getValue(), "verified")).isEqualTo(true);
    assertThat(response.email()).isEqualTo(request.email());
  }

  @Test
  void createSpecialistMarksSpecialistsAsVerified() {
    SpecialistRequest request =
        new SpecialistRequest(
            "specialist@example.com",
            "Morgan Specialist",
            "+15555550102",
            "Electrician",
            List.of("wiring", "inspection"),
            new BigDecimal("90.00"));

    when(userRepository.existsByEmail(request.email())).thenReturn(false);
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(
            invocation -> {
              UserEntity saved = invocation.getArgument(0);
              ReflectionTestUtils.setField(saved, "id", new ObjectId());
              return saved;
            });

    SpecialistResponse response = userService.createSpecialist(request);

    ArgumentCaptor<UserEntity> savedUser = ArgumentCaptor.forClass(UserEntity.class);
    verify(userRepository).save(savedUser.capture());

    assertThat(ReflectionTestUtils.getField(savedUser.getValue(), "role"))
        .isEqualTo(Role.SPECIALIST);
    assertThat(ReflectionTestUtils.getField(savedUser.getValue(), "verified")).isEqualTo(true);
    assertThat(ReflectionTestUtils.getField(savedUser.getValue(), "hourlyRate"))
        .isEqualTo(new BigDecimal("90.00"));
    assertThat(response.email()).isEqualTo(request.email());
  }

  @Test
  void updateCustomerRejectsChangingEmailToExistingUser() {
    String customerId = new ObjectId().toString();
    CustomerRequest request =
        new CustomerRequest(
            "new@example.com", "Jamie Customer", "+15555550101", "123 Main St", "PAYPAL");
    UserEntity existingCustomer = new UserEntity();
    ReflectionTestUtils.setField(existingCustomer, "id", new ObjectId(customerId));
    ReflectionTestUtils.setField(existingCustomer, "email", "current@example.com");
    ReflectionTestUtils.setField(existingCustomer, "role", Role.CUSTOMER);

    when(userRepository.findByIdAndRole(new ObjectId(customerId), Role.CUSTOMER))
        .thenReturn(java.util.Optional.of(existingCustomer));
    when(userRepository.existsByEmail(request.email())).thenReturn(true);

    assertThatThrownBy(() -> userService.updateCustomer(customerId, request))
        .isInstanceOf(ConflictException.class)
        .hasMessage("User with email new@example.com already exists");
  }

  @Test
  void getCustomerByEmailReturnsCustomer() {
    UserEntity customer = new UserEntity();
    ReflectionTestUtils.setField(customer, "id", new ObjectId());
    ReflectionTestUtils.setField(customer, "email", "customer@example.com");
    ReflectionTestUtils.setField(customer, "name", "Jamie Customer");
    ReflectionTestUtils.setField(customer, "role", Role.CUSTOMER);

    when(userRepository.findByEmail("customer@example.com"))
        .thenReturn(java.util.Optional.of(customer));

    CustomerResponse response = userService.getCustomerByEmail("customer@example.com");

    assertThat(response.email()).isEqualTo("customer@example.com");
    assertThat(response.name()).isEqualTo("Jamie Customer");
  }

  @Test
  void getCustomerByIdRejectsMalformedIdBeforeRepositoryAccess() {
    assertThatThrownBy(() -> userService.getCustomerById("not-an-object-id"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid customer ID format.");

    verifyNoInteractions(userRepository);
  }

  @Test
  void updateCustomerRejectsMalformedIdBeforeRepositoryAccess() {
    CustomerRequest request =
        new CustomerRequest(
            "customer@example.com", "Jamie Customer", "+15555550101", "123 Main St", "PAYPAL");

    assertThatThrownBy(() -> userService.updateCustomer("not-an-object-id", request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid customer ID format.");

    verifyNoInteractions(userRepository);
  }

  @Test
  void deleteSpecialistRejectsMalformedIdBeforeRepositoryAccess() {
    assertThatThrownBy(() -> userService.deleteSpecialist("not-an-object-id"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid specialist ID format.");

    verifyNoInteractions(userRepository);
  }
}
