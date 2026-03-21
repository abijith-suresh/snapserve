package com.snapserve.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    when(userServiceClient.getCustomerById(request.customerId()))
        .thenReturn(ApiResponse.ok(customer));
    when(userServiceClient.getSpecialistById(request.specialistId()))
        .thenReturn(ApiResponse.ok(specialistResponse()));
    when(bookingRepository.findConflictingBookings(
            request.specialistId(),
            request.bookingDate().minusHours(1),
            request.bookingDate().plusHours(1)))
        .thenReturn(List.of());
    when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

    BookingResponse response = bookingService.createBooking(request);

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

    when(userServiceClient.getCustomerById(request.customerId()))
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

    BookingResponse response = bookingService.createBooking(request);

    assertThat(response.id())
        .isEqualTo(((ObjectId) ReflectionTestUtils.getField(savedBooking, "id")).toString());
    assertThat(response.status()).isEqualTo("PENDING");
    verify(bookingRepository).save(any(Booking.class));
    verify(bookingNotificationDispatcher).sendBookingCreatedConfirmation(savedBooking, customer);
  }

  @Test
  void updateBookingRejectsSkippingFromPendingToCompleted() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));

    assertThatThrownBy(
            () ->
                bookingService.updateBooking(
                    bookingId, new UpdateBookingRequest(null, "COMPLETED", null)))
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

    assertThatThrownBy(
            () ->
                bookingService.updateBooking(
                    bookingId, new UpdateBookingRequest(null, "CONFIRMED", null)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Booking cannot transition from CANCELLED to CONFIRMED.");

    verify(bookingRepository, never()).save(any(Booking.class));
  }

  @Test
  void updateBookingAllowsPendingToConfirmedTransition() {
    Booking booking = bookingWithStatus("PENDING");
    String bookingId = ((ObjectId) ReflectionTestUtils.getField(booking, "id")).toString();

    when(bookingRepository.findById((ObjectId) ReflectionTestUtils.getField(booking, "id")))
        .thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(Booking.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BookingResponse response =
        bookingService.updateBooking(bookingId, new UpdateBookingRequest(null, "CONFIRMED", null));

    assertThat(response.status()).isEqualTo("CONFIRMED");
    verify(bookingRepository).save(booking);
  }

  @Test
  void getBookingByIdRejectsMalformedIdBeforeRepositoryAccess() {
    assertThatThrownBy(() -> bookingService.getBookingById("not-an-object-id"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid booking ID format.");

    verifyNoInteractions(bookingRepository);
  }

  @Test
  void deleteBookingRejectsMalformedIdBeforeRepositoryAccess() {
    assertThatThrownBy(() -> bookingService.deleteBooking("not-an-object-id"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid booking ID format.");

    verifyNoInteractions(bookingRepository);
  }

  private Booking bookingWithStatus(String status) {
    Booking booking = new Booking();
    ReflectionTestUtils.setField(booking, "id", new ObjectId());
    ReflectionTestUtils.setField(booking, "customerId", "customer-1");
    ReflectionTestUtils.setField(booking, "specialistId", "specialist-1");
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
