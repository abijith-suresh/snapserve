package com.snapserve.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.snapserve.booking.dto.request.BookingRequest;
import com.snapserve.booking.dto.request.UpdateBookingRequest;
import com.snapserve.booking.dto.response.BookingResponse;
import com.snapserve.booking.model.Booking;
import com.snapserve.booking.repository.BookingRepository;
import com.snapserve.booking.service.mapper.BookingMapper;
import com.snapserve.common.exception.BadRequestException;
import com.snapserve.common.exception.ForbiddenException;
import com.snapserve.common.exception.ServiceUnavailableException;
import com.snapserve.common.response.ApiResponse;
import com.snapserve.userclient.client.UserServiceClient;
import com.snapserve.userclient.dto.customer.CustomerResponse;
import com.snapserve.userclient.dto.specialist.SpecialistResponse;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  @Mock private BookingRepository bookingRepository;
  @Mock private UserServiceClient userServiceClient;
  @Mock private BookingNotificationDispatcher bookingNotificationDispatcher;

  private BookingService bookingService;

  @BeforeEach
  void setUp() throws Exception {
    BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    Constructor<BookingService> constructor =
        BookingService.class.getDeclaredConstructor(
            BookingRepository.class,
            BookingMapper.class,
            UserServiceClient.class,
            BookingNotificationDispatcher.class);
    constructor.setAccessible(true);
    bookingService =
        constructor.newInstance(
            bookingRepository, bookingMapper, userServiceClient, bookingNotificationDispatcher);
  }

  @Test
  void createBookingDispatchesConfirmationAfterPersistingBooking() {
    BookingRequest request = bookingRequest();
    CustomerResponse customer = customerResponse();
    Booking savedBooking = savedBooking(request);

    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customer));
    when(userServiceClient.getSpecialistById(request.specialistId()))
        .thenReturn(ApiResponse.ok(specialistResponse()));
    when(bookingRepository.findConflictingBookings(
            request.specialistId(),
            request.bookingDate().minusHours(1),
            request.bookingDate().plusHours(1)))
        .thenReturn(List.of());
    when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

    BookingResponse response =
        bookingService.createBooking("customer@snapserve.com", "CUSTOMER", request);

    assertThat(response.id())
        .isEqualTo(((ObjectId) ReflectionTestUtils.getField(savedBooking, "id")).toString());
    assertThat(response.status()).isEqualTo("PENDING");
    InOrder inOrder = Mockito.inOrder(bookingRepository, bookingNotificationDispatcher);
    inOrder.verify(bookingRepository).save(any(Booking.class));
    inOrder
        .verify(bookingNotificationDispatcher)
        .sendBookingCreatedConfirmation(savedBooking, customer);
  }

  @Test
  void createBookingKeepsPersistedBookingWhenNotificationDispatchFails() {
    BookingRequest request = bookingRequest();
    CustomerResponse customer = customerResponse();
    Booking savedBooking = savedBooking(request);

    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customer));
    when(userServiceClient.getSpecialistById(request.specialistId()))
        .thenReturn(ApiResponse.ok(specialistResponse()));
    when(bookingRepository.findConflictingBookings(
            request.specialistId(),
            request.bookingDate().minusHours(1),
            request.bookingDate().plusHours(1)))
        .thenReturn(List.of());
    when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
    doThrow(new RuntimeException("notification service unavailable"))
        .when(bookingNotificationDispatcher)
        .sendBookingCreatedConfirmation(savedBooking, customer);

    BookingResponse response =
        bookingService.createBooking("customer@snapserve.com", "CUSTOMER", request);

    assertThat(response.id())
        .isEqualTo(((ObjectId) ReflectionTestUtils.getField(savedBooking, "id")).toString());
    assertThat(response.status()).isEqualTo("PENDING");
    verify(bookingRepository).save(any(Booking.class));
    verify(bookingNotificationDispatcher).sendBookingCreatedConfirmation(savedBooking, customer);
  }

  @Test
  void createBookingBindsAuthenticatedCustomerInsteadOfCallerSuppliedCustomerId() {
    BookingRequest request =
        new BookingRequest(
            "spoofed-customer",
            "specialist-1",
            LocalDateTime.of(2026, 4, 1, 10, 0),
            "Fix kitchen sink",
            BigDecimal.valueOf(149.99),
            "Plumbing");
    CustomerResponse customer = customerResponse();

    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customer));
    when(userServiceClient.getSpecialistById(request.specialistId()))
        .thenReturn(ApiResponse.ok(specialistResponse()));
    when(bookingRepository.findConflictingBookings(
            request.specialistId(),
            request.bookingDate().minusHours(1),
            request.bookingDate().plusHours(1)))
        .thenReturn(List.of());
    when(bookingRepository.save(any(Booking.class)))
        .thenAnswer(
            invocation -> {
              Booking persistedBooking = invocation.getArgument(0);
              ReflectionTestUtils.setField(persistedBooking, "id", new ObjectId());
              return persistedBooking;
            });

    BookingResponse response =
        bookingService.createBooking("customer@snapserve.com", "CUSTOMER", request);

    org.mockito.ArgumentCaptor<Booking> savedBooking =
        org.mockito.ArgumentCaptor.forClass(Booking.class);
    verify(bookingRepository).save(savedBooking.capture());
    assertThat(ReflectionTestUtils.getField(savedBooking.getValue(), "customerId"))
        .isEqualTo("customer-1");
    assertThat(response.customerId()).isEqualTo("customer-1");
    verify(userServiceClient).getCustomerByEmail("customer@snapserve.com");
    verify(userServiceClient, never()).getCustomerById("spoofed-customer");
  }

  @Test
  void createBookingRejectsNonCustomerRole() {
    assertThatThrownBy(
            () ->
                bookingService.createBooking(
                    "specialist@snapserve.com", "SPECIALIST", bookingRequest()))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Only customers can create bookings.");

    verifyNoInteractions(userServiceClient, bookingRepository, bookingNotificationDispatcher);
  }

  @Test
  void createBookingRejectsRoleNamesThatOnlyContainCustomerAsSubstring() {
    assertThatThrownBy(
            () ->
                bookingService.createBooking(
                    "customer@snapserve.com", "SUPERCUSTOMER", bookingRequest()))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Only customers can create bookings.");

    verifyNoInteractions(userServiceClient, bookingRepository, bookingNotificationDispatcher);
  }

  @Test
  void getBookingsByCustomerRejectsAccessToAnotherCustomersBookings() {
    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse()));

    assertThatThrownBy(
            () ->
                bookingService.getBookingsByCustomer(
                    "customer-2", "customer@snapserve.com", "CUSTOMER", PageRequest.of(0, 20)))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("You can only view your own bookings.");

    verify(userServiceClient).getCustomerByEmail("customer@snapserve.com");
    verify(bookingRepository, never()).findByCustomerId(eq("customer-2"), any());
  }

  @Test
  void getBookingByIdRejectsCustomerAccessingAnotherCustomersBooking() {
    Booking booking = bookingWithCustomerAndSpecialist("customer-2", "specialist-1", "PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse()));

    assertThatThrownBy(
            () -> bookingService.getBookingById(bookingId, "customer@snapserve.com", "CUSTOMER"))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("You can only access your own or assigned bookings.");
  }

  @Test
  void getBookingByIdAllowsAssignedSpecialist() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getSpecialistById("specialist-1"))
        .thenReturn(ApiResponse.ok(specialistResponse()));

    BookingResponse response =
        bookingService.getBookingById(bookingId, "morgan@example.com", "SPECIALIST");

    assertThat(response.id()).isEqualTo(bookingId);
  }

  @Test
  void updateBookingRejectsSkippingFromPendingToCompleted() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getSpecialistById("specialist-1"))
        .thenReturn(ApiResponse.ok(specialistResponse()));

    assertThatThrownBy(
            () ->
                bookingService.updateBooking(
                    bookingId,
                    "morgan@example.com",
                    "SPECIALIST",
                    new UpdateBookingRequest(null, "COMPLETED", null)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Booking cannot transition from PENDING to COMPLETED.");

    verify(bookingRepository, never()).save(any(Booking.class));
  }

  @Test
  void updateBookingRejectsChangingCancelledBooking() {
    Booking booking = bookingWithStatus("CANCELLED");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getSpecialistById("specialist-1"))
        .thenReturn(ApiResponse.ok(specialistResponse()));

    assertThatThrownBy(
            () ->
                bookingService.updateBooking(
                    bookingId,
                    "morgan@example.com",
                    "SPECIALIST",
                    new UpdateBookingRequest(null, "CONFIRMED", null)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Booking cannot transition from CANCELLED to CONFIRMED.");

    verify(bookingRepository, never()).save(any(Booking.class));
  }

  @Test
  void updateBookingRejectsCustomerChangingAnotherCustomersBooking() {
    Booking booking = bookingWithCustomerAndSpecialist("customer-2", "specialist-1", "PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse()));

    assertThatThrownBy(
            () ->
                bookingService.updateBooking(
                    bookingId,
                    "customer@snapserve.com",
                    "CUSTOMER",
                    new UpdateBookingRequest(null, "CANCELLED", null)))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("You can only update your own or assigned bookings.");

    verify(bookingRepository, never()).save(any(Booking.class));
  }

  @Test
  void updateBookingRejectsCustomerConfirmingOwnBooking() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse()));

    assertThatThrownBy(
            () ->
                bookingService.updateBooking(
                    bookingId,
                    "customer@snapserve.com",
                    "CUSTOMER",
                    new UpdateBookingRequest(null, "CONFIRMED", null)))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Customers can only cancel their own bookings.");

    verify(bookingRepository, never()).save(any(Booking.class));
  }

  @Test
  void updateBookingRejectsCustomerCancellingOwnBookingWhileChangingNotes() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse()));

    assertThatThrownBy(
            () ->
                bookingService.updateBooking(
                    bookingId,
                    "customer@snapserve.com",
                    "CUSTOMER",
                    new UpdateBookingRequest(null, "CANCELLED", "Changed notes")))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Customers can only cancel their own bookings.");

    verify(bookingRepository, never()).save(any(Booking.class));
  }

  @Test
  void updateBookingAllowsPendingToConfirmedTransition() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getSpecialistById("specialist-1"))
        .thenReturn(ApiResponse.ok(specialistResponse()));
    when(bookingRepository.save(any(Booking.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BookingResponse response =
        bookingService.updateBooking(
            bookingId,
            "morgan@example.com",
            "SPECIALIST",
            new UpdateBookingRequest(null, "CONFIRMED", null));

    assertThat(response.status()).isEqualTo("CONFIRMED");
    verify(bookingRepository).save(booking);
  }

  @Test
  void updateBookingDispatchesCancellationNotificationOnPendingToCancelledTransition() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();
    CustomerResponse customer = customerResponse();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse()));
    when(bookingRepository.save(any(Booking.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(userServiceClient.getCustomerById("customer-1")).thenReturn(ApiResponse.ok(customer));

    BookingResponse response =
        bookingService.updateBooking(
            bookingId,
            "customer@snapserve.com",
            "CUSTOMER",
            new UpdateBookingRequest(null, "CANCELLED", null));

    assertThat(response.status()).isEqualTo("CANCELLED");
    InOrder inOrder =
        Mockito.inOrder(bookingRepository, userServiceClient, bookingNotificationDispatcher);
    inOrder.verify(userServiceClient).getCustomerByEmail("customer@snapserve.com");
    inOrder.verify(bookingRepository).save(booking);
    inOrder.verify(userServiceClient).getCustomerById("customer-1");
    inOrder
        .verify(bookingNotificationDispatcher)
        .sendBookingCancelledNotification(booking, customer);
  }

  @Test
  void updateBookingDispatchesCompletionNotificationOnConfirmedToCompletedTransition() {
    Booking booking = bookingWithStatus("CONFIRMED");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();
    CustomerResponse customer = customerResponse();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getSpecialistById("specialist-1"))
        .thenReturn(ApiResponse.ok(specialistResponse()));
    when(bookingRepository.save(any(Booking.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(userServiceClient.getCustomerById("customer-1")).thenReturn(ApiResponse.ok(customer));

    BookingResponse response =
        bookingService.updateBooking(
            bookingId,
            "morgan@example.com",
            "SPECIALIST",
            new UpdateBookingRequest(null, "COMPLETED", null));

    assertThat(response.status()).isEqualTo("COMPLETED");
    InOrder inOrder =
        Mockito.inOrder(bookingRepository, userServiceClient, bookingNotificationDispatcher);
    inOrder.verify(userServiceClient).getSpecialistById("specialist-1");
    inOrder.verify(bookingRepository).save(booking);
    inOrder.verify(userServiceClient).getCustomerById("customer-1");
    inOrder
        .verify(bookingNotificationDispatcher)
        .sendBookingCompletedNotification(booking, customer);
  }

  @Test
  void updateBookingDoesNotDispatchStatusNotificationWhenStatusIsUnchanged() {
    Booking booking = bookingWithStatus("CONFIRMED");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getSpecialistById("specialist-1"))
        .thenReturn(ApiResponse.ok(specialistResponse()));
    when(bookingRepository.save(any(Booking.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BookingResponse response =
        bookingService.updateBooking(
            bookingId,
            "morgan@example.com",
            "SPECIALIST",
            new UpdateBookingRequest(null, "CONFIRMED", null));

    assertThat(response.status()).isEqualTo("CONFIRMED");
    verify(bookingRepository).save(booking);
    verify(userServiceClient).getSpecialistById("specialist-1");
    verifyNoInteractions(bookingNotificationDispatcher);
  }

  @Test
  void updateBookingKeepsPersistedStatusChangeWhenNotificationDispatchFails() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();
    CustomerResponse customer = customerResponse();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse()));
    when(bookingRepository.save(any(Booking.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(userServiceClient.getCustomerById("customer-1")).thenReturn(ApiResponse.ok(customer));
    doThrow(new RuntimeException("notification service unavailable"))
        .when(bookingNotificationDispatcher)
        .sendBookingCancelledNotification(booking, customer);

    BookingResponse response =
        bookingService.updateBooking(
            bookingId,
            "customer@snapserve.com",
            "CUSTOMER",
            new UpdateBookingRequest(null, "CANCELLED", null));

    assertThat(response.status()).isEqualTo("CANCELLED");
    verify(bookingRepository).save(booking);
    verify(bookingNotificationDispatcher).sendBookingCancelledNotification(booking, customer);
  }

  @Test
  void updateBookingKeepsPersistedStatusChangeWhenCustomerLookupForNotificationFails() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse()));
    when(bookingRepository.save(any(Booking.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(userServiceClient.getCustomerById("customer-1"))
        .thenThrow(new ServiceUnavailableException("user service unavailable"));

    BookingResponse response =
        bookingService.updateBooking(
            bookingId,
            "customer@snapserve.com",
            "CUSTOMER",
            new UpdateBookingRequest(null, "CANCELLED", null));

    assertThat(response.status()).isEqualTo("CANCELLED");
    verify(bookingRepository).save(booking);
    verify(userServiceClient).getCustomerById("customer-1");
    verifyNoInteractions(bookingNotificationDispatcher);
  }

  @Test
  void deleteBookingRejectsAssignedSpecialist() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));

    assertThatThrownBy(
            () -> bookingService.deleteBooking(bookingId, "morgan@example.com", "SPECIALIST"))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Only customers can delete their own pending bookings.");

    verify(bookingRepository, never()).delete(any(Booking.class));
  }

  @Test
  void deleteBookingRejectsCustomerDeletingNonPendingBooking() {
    Booking booking = bookingWithStatus("CONFIRMED");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse()));

    assertThatThrownBy(
            () -> bookingService.deleteBooking(bookingId, "customer@snapserve.com", "CUSTOMER"))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Only pending bookings can be deleted.");

    verify(bookingRepository, never()).delete(any(Booking.class));
  }

  @Test
  void deleteBookingAllowsCustomerDeletingOwnPendingBooking() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(userServiceClient.getCustomerByEmail("customer@snapserve.com"))
        .thenReturn(ApiResponse.ok(customerResponse()));

    bookingService.deleteBooking(bookingId, "customer@snapserve.com", "CUSTOMER");

    verify(bookingRepository).delete(booking);
  }

  @Test
  void getBookingByIdRejectsMalformedIdBeforeRepositoryAccess() {
    assertThatThrownBy(
            () ->
                bookingService.getBookingById(
                    "not-an-object-id", "customer@snapserve.com", "CUSTOMER"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid booking ID format.");

    verifyNoInteractions(bookingRepository);
  }

  @Test
  void deleteBookingRejectsMalformedIdBeforeRepositoryAccess() {
    assertThatThrownBy(
            () ->
                bookingService.deleteBooking(
                    "not-an-object-id", "customer@snapserve.com", "CUSTOMER"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid booking ID format.");

    verifyNoInteractions(bookingRepository);
  }

  private Booking bookingWithStatus(String status) {
    return bookingWithCustomerAndSpecialist("customer-1", "specialist-1", status);
  }

  private Booking bookingWithCustomerAndSpecialist(
      String customerId, String specialistId, String status) {
    Booking booking = new Booking();
    ReflectionTestUtils.setField(booking, "id", new ObjectId());
    ReflectionTestUtils.setField(booking, "customerId", customerId);
    ReflectionTestUtils.setField(booking, "specialistId", specialistId);
    ReflectionTestUtils.setField(booking, "bookingDate", LocalDateTime.of(2026, 4, 1, 10, 0));
    ReflectionTestUtils.setField(booking, "status", status);
    return booking;
  }

  private BookingRequest bookingRequest() {
    return new BookingRequest(
        "customer-1",
        "specialist-1",
        LocalDateTime.of(2026, 4, 1, 10, 0),
        "Fix kitchen sink",
        BigDecimal.valueOf(149.99),
        "Plumbing");
  }

  private CustomerResponse customerResponse() {
    return new CustomerResponse(
        "customer-1", "jamie@example.com", "Jamie", "555-0100", "Main St", "CARD", null, null);
  }

  private SpecialistResponse specialistResponse() {
    return new SpecialistResponse(
        "specialist-1",
        "morgan@example.com",
        "Morgan",
        "555-0101",
        "Plumber",
        List.of("Plumbing"),
        BigDecimal.valueOf(99.99),
        true,
        null,
        null);
  }

  private Booking savedBooking(BookingRequest request) {
    Booking booking = new Booking();
    ReflectionTestUtils.setField(booking, "id", new ObjectId());
    ReflectionTestUtils.setField(booking, "customerId", request.customerId());
    ReflectionTestUtils.setField(booking, "specialistId", request.specialistId());
    ReflectionTestUtils.setField(booking, "bookingDate", request.bookingDate());
    ReflectionTestUtils.setField(booking, "notes", request.notes());
    ReflectionTestUtils.setField(booking, "price", request.price());
    ReflectionTestUtils.setField(booking, "serviceType", request.serviceType());
    ReflectionTestUtils.setField(booking, "status", "PENDING");
    return booking;
  }
}
