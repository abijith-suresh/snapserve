package com.snapserve.booking.service;

import com.snapserve.booking.dto.request.BookingRequest;
import com.snapserve.booking.dto.request.UpdateBookingRequest;
import com.snapserve.booking.dto.response.BookingListResponse;
import com.snapserve.booking.dto.response.BookingResponse;
import com.snapserve.booking.model.Booking;
import com.snapserve.booking.repository.BookingRepository;
import com.snapserve.booking.service.mapper.BookingMapper;
import com.snapserve.common.exception.ConflictException;
import com.snapserve.common.exception.ResourceNotFoundException;
import com.snapserve.common.exception.ServiceUnavailableException;
import com.snapserve.common.mongo.ObjectIdParser;
import com.snapserve.userclient.client.UserServiceClient;
import com.snapserve.userclient.dto.customer.CustomerResponse;
import feign.FeignException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

  private final BookingRepository bookingRepository;
  private final BookingMapper bookingMapper;
  private final UserServiceClient userServiceClient;
  private final BookingNotificationDispatcher bookingNotificationDispatcher;

  @Transactional(readOnly = true)
  public BookingResponse getBookingById(String id) {
    log.info("Fetching booking with id: {}", id);

    Booking booking =
        bookingRepository
            .findById(parseObjectId(id, "booking"))
            .orElseThrow(() -> ResourceNotFoundException.of("Booking", id));

    log.debug("Found booking: {}", booking);
    return bookingMapper.toResponse(booking);
  }

  @Transactional(readOnly = true)
  public BookingListResponse getBookingsByCustomer(String customerId, Pageable pageable) {
    log.info("Fetching bookings for customer: {} with pagination: {}", customerId, pageable);

    validateCustomerExists(customerId);

    Page<Booking> bookingPage = bookingRepository.findByCustomerId(customerId, pageable);

    log.debug("Found {} bookings for customer {}", bookingPage.getTotalElements(), customerId);
    return toBookingListResponse(bookingPage);
  }

  @Transactional(readOnly = true)
  public BookingListResponse getBookingsBySpecialist(String specialistId, Pageable pageable) {
    log.info("Fetching bookings for specialist: {} with pagination: {}", specialistId, pageable);

    validateSpecialistExists(specialistId);

    Page<Booking> bookingPage = bookingRepository.findBySpecialistId(specialistId, pageable);

    log.debug("Found {} bookings for specialist {}", bookingPage.getTotalElements(), specialistId);
    return toBookingListResponse(bookingPage);
  }

  @Transactional(readOnly = true)
  public BookingListResponse getBookingsByCustomerAndStatus(
      String customerId, String status, Pageable pageable) {
    log.info(
        "Fetching bookings for customer: {} with status: {} and pagination: {}",
        customerId,
        status,
        pageable);

    validateCustomerExists(customerId);

    Page<Booking> bookingPage =
        bookingRepository.findByCustomerIdAndStatus(customerId, status, pageable);

    log.debug(
        "Found {} bookings for customer {} with status {}",
        bookingPage.getTotalElements(),
        customerId,
        status);
    return toBookingListResponse(bookingPage);
  }

  @Transactional(readOnly = true)
  public BookingListResponse getBookingsBySpecialistAndStatus(
      String specialistId, String status, Pageable pageable) {
    log.info(
        "Fetching bookings for specialist: {} with status: {} and pagination: {}",
        specialistId,
        status,
        pageable);

    validateSpecialistExists(specialistId);

    Page<Booking> bookingPage =
        bookingRepository.findBySpecialistIdAndStatus(specialistId, status, pageable);

    log.debug(
        "Found {} bookings for specialist {} with status {}",
        bookingPage.getTotalElements(),
        specialistId,
        status);
    return toBookingListResponse(bookingPage);
  }

  @Transactional(readOnly = true)
  public BookingListResponse getAllBookings(Pageable pageable) {
    log.info("Fetching all bookings with pagination: {}", pageable);

    Page<Booking> bookingPage = bookingRepository.findAll(pageable);

    log.debug("Found {} total bookings", bookingPage.getTotalElements());
    return toBookingListResponse(bookingPage);
  }

  @Transactional
  public BookingResponse createBooking(BookingRequest request) {
    log.info(
        "Creating booking for customer: {} with specialist: {}",
        request.customerId(),
        request.specialistId());

    CustomerResponse customer = requireCustomer(request.customerId());
    validateSpecialistExists(request.specialistId());
    checkForBookingConflicts(request.specialistId(), request.bookingDate());

    Booking booking = bookingMapper.toEntity(request);
    booking.setStatus("PENDING");

    Booking savedBooking = bookingRepository.save(booking);

    log.info("Booking created successfully with id: {}", savedBooking.getId());

    try {
      bookingNotificationDispatcher.sendBookingCreatedConfirmation(savedBooking, customer);
    } catch (RuntimeException ex) {
      log.error(
          "Booking {} was persisted but confirmation notification failed: {}",
          savedBooking.getId(),
          ex.getMessage(),
          ex);
    }

    return bookingMapper.toResponse(savedBooking);
  }

  @Transactional
  public BookingResponse updateBooking(String id, UpdateBookingRequest request) {
    log.info("Updating booking with id: {}", id);

    Booking booking =
        bookingRepository
            .findById(parseObjectId(id, "booking"))
            .orElseThrow(() -> ResourceNotFoundException.of("Booking", id));

    if (request.bookingDate() != null) {
      checkForBookingConflicts(booking.getSpecialistId(), request.bookingDate(), id);
    }

    validateStatusTransition(booking.getStatus(), request.status());

    bookingMapper.updateEntityFromRequest(request, booking);

    Booking updatedBooking = bookingRepository.save(booking);

    log.info("Booking updated successfully with id: {}", id);

    // TODO: Publish async event for notification-service to send update email
    // eventPublisher.publishEvent(new BookingUpdatedEvent(updatedBooking));

    return bookingMapper.toResponse(updatedBooking);
  }

  @Transactional
  public void deleteBooking(String id) {
    log.info("Deleting booking with id: {}", id);

    ObjectId objectId = parseObjectId(id, "booking");
    Booking booking =
        bookingRepository
            .findById(objectId)
            .orElseThrow(() -> ResourceNotFoundException.of("Booking", id));

    bookingRepository.delete(booking);

    log.info("Booking deleted successfully with id: {}", id);

    // TODO: Publish async event for notification-service to send cancellation email
    // eventPublisher.publishEvent(new BookingCancelledEvent(booking));
  }

  @Transactional(readOnly = true)
  public void validateCustomerExists(String customerId) {
    log.debug("Validating customer exists: {}", customerId);

    requireCustomer(customerId);
    log.debug("Customer {} validated successfully", customerId);
  }

  @Transactional(readOnly = true)
  public CustomerResponse requireCustomer(String customerId) {
    log.debug("Loading customer: {}", customerId);

    try {
      return requireResponseBody(
          userServiceClient.getCustomerById(customerId), "customer", customerId);
    } catch (FeignException.NotFound e) {
      log.warn("Customer not found: {}", customerId);
      throw ResourceNotFoundException.of("Customer", customerId);
    } catch (FeignException e) {
      log.error("Error validating customer {}: {}", customerId, e.getMessage());
      throw new ServiceUnavailableException("Unable to validate customer. Please try again later.");
    }
  }

  @Transactional(readOnly = true)
  public void validateSpecialistExists(String specialistId) {
    log.debug("Validating specialist exists: {}", specialistId);

    try {
      requireResponseBody(
          userServiceClient.getSpecialistById(specialistId), "specialist", specialistId);
      log.debug("Specialist {} validated successfully", specialistId);
    } catch (FeignException.NotFound e) {
      log.warn("Specialist not found: {}", specialistId);
      throw ResourceNotFoundException.of("Specialist", specialistId);
    } catch (FeignException e) {
      log.error("Error validating specialist {}: {}", specialistId, e.getMessage());
      throw new ServiceUnavailableException(
          "Unable to validate specialist. Please try again later.");
    }
  }

  private void checkForBookingConflicts(String specialistId, LocalDateTime bookingDate) {
    checkForBookingConflicts(specialistId, bookingDate, null);
  }

  private void checkForBookingConflicts(
      String specialistId, LocalDateTime bookingDate, String excludeBookingId) {
    log.debug(
        "Checking for booking conflicts for specialist: {} at time: {}", specialistId, bookingDate);

    LocalDateTime conflictWindowStart = bookingDate.minus(1, ChronoUnit.HOURS);
    LocalDateTime conflictWindowEnd = bookingDate.plus(1, ChronoUnit.HOURS);

    List<Booking> conflictingBookings =
        bookingRepository.findConflictingBookings(
            specialistId, conflictWindowStart, conflictWindowEnd);

    if (excludeBookingId != null) {
      conflictingBookings =
          conflictingBookings.stream()
              .filter(b -> !b.getId().toString().equals(excludeBookingId))
              .toList();
    }

    if (!conflictingBookings.isEmpty()) {
      log.warn(
          "Booking conflict detected for specialist: {} at time: {}", specialistId, bookingDate);
      throw new ConflictException(
          "The specialist is already booked during the requested time slot. "
              + "Please choose a different time.");
    }

    log.debug(
        "No booking conflicts found for specialist: {} at time: {}", specialistId, bookingDate);
  }

  private void validateStatusTransition(String currentStatus, String requestedStatus) {
    if (requestedStatus == null || requestedStatus.equals(currentStatus)) {
      return;
    }

    Map<String, List<String>> allowedTransitions =
        Map.of(
            "PENDING", List.of("CONFIRMED", "CANCELLED"),
            "CONFIRMED", List.of("COMPLETED", "CANCELLED"),
            "CANCELLED", List.of(),
            "COMPLETED", List.of());

    List<String> nextStatuses = allowedTransitions.getOrDefault(currentStatus, List.of());
    if (!nextStatuses.contains(requestedStatus)) {
      throw new com.snapserve.common.exception.BadRequestException(
          "Booking cannot transition from " + currentStatus + " to " + requestedStatus + ".");
    }
  }

  private BookingListResponse toBookingListResponse(Page<Booking> page) {
    return new BookingListResponse(
        bookingMapper.toResponseList(page.getContent()),
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

  private <T> T requireResponseBody(
      com.snapserve.common.response.ApiResponse<T> response,
      String resourceName,
      String identifier) {
    if (response == null || response.getData() == null) {
      throw new ServiceUnavailableException(
          "Unable to validate " + resourceName + ". Please try again later.");
    }

    return response.getData();
  }
}
