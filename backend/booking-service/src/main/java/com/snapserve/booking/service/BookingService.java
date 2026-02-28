package com.snapserve.booking.service;

import com.snapserve.booking.dto.AddBookingDto;
import com.snapserve.booking.dto.BookingResponseDto;
import com.snapserve.booking.dto.CustomerDto;
import com.snapserve.booking.dto.SpecialistDto;
import com.snapserve.booking.model.Booking;
import com.snapserve.booking.repo.BookingRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class BookingService {

  @Value("${user.service.url}")
  private String userServiceUrl;

  @Value("${notification.service.url}")
  private String notificationServiceUrl;

  @Autowired private BookingRepository bookingRepo;

  @Autowired private RestClient.Builder restClientBuilder;

  private Booking dtoToModel(AddBookingDto bookingDto) {
    Booking booking = new Booking();
    booking.setCustomerId(new ObjectId(bookingDto.getCustomerId()));
    booking.setSpecialistId(new ObjectId(bookingDto.getSpecialistId()));
    booking.setBookingDate(bookingDto.getBookingDate());
    booking.setAppointmentTime(bookingDto.getAppointmentTime());
    booking.setPrice(bookingDto.getPrice());
    booking.setService(bookingDto.getService());
    booking.setStatus(bookingDto.getStatus());
    return booking;
  }

  private BookingResponseDto toResponseDto(Booking booking) {
    CustomerDto customer = fetchCustomer(booking.getCustomerId().toString());
    SpecialistDto specialist = fetchSpecialist(booking.getSpecialistId().toString());
    return new BookingResponseDto(
        booking.getId().toString(),
        customer,
        specialist,
        booking.getBookingDate(),
        booking.getAppointmentTime(),
        booking.getService(),
        booking.getStatus(),
        booking.getPrice());
  }

  public String createBooking(AddBookingDto bookingDto) {
    Booking newBooking = dtoToModel(bookingDto);
    bookingRepo.save(newBooking);
    SpecialistDto specialist = fetchSpecialist(bookingDto.getSpecialistId());
    if (specialist != null) {
      sendNewBookingEmail(specialist, bookingDto);
    }
    return "Booking created successfully.";
  }

  public List<BookingResponseDto> getAllBookings() {
    return bookingRepo.findAll().stream().map(this::toResponseDto).collect(Collectors.toList());
  }

  public BookingResponseDto getBookingById(ObjectId id) {
    Optional<Booking> booking = bookingRepo.findById(id);
    return booking.map(this::toResponseDto).orElse(null);
  }

  public Booking updateBooking(ObjectId id, Booking bookingDetails) {
    bookingDetails.setId(id);
    return bookingRepo.save(bookingDetails);
  }

  public void deleteBooking(ObjectId id) {
    bookingRepo.deleteById(id);
  }

  public List<BookingResponseDto> getBookingsByCustomerId(ObjectId customerId) {
    return bookingRepo.findByCustomerId(customerId).stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());
  }

  public List<BookingResponseDto> getBookingsBySpecialistId(ObjectId specialistId) {
    return bookingRepo.findBySpecialistId(specialistId).stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());
  }

  public void updateBookingStatus(ObjectId id, String status) {
    bookingRepo
        .findById(id)
        .ifPresent(
            booking -> {
              booking.setStatus(status);
              Booking saved = bookingRepo.save(booking);
              CustomerDto customer = fetchCustomer(saved.getCustomerId().toString());
              SpecialistDto specialist = fetchSpecialist(saved.getSpecialistId().toString());
              if (customer != null) {
                sendBookingStatusEmail(customer.getEmail(), customer.getName(), status);
              }
              if (specialist != null) {
                sendBookingStatusEmail(specialist.getEmail(), specialist.getName(), status);
              }
            });
  }

  CustomerDto fetchCustomer(String id) {
    try {
      return restClientBuilder
          .build()
          .get()
          .uri(userServiceUrl + "/api/v1/customers/{id}", id)
          .retrieve()
          .body(CustomerDto.class);
    } catch (RestClientException e) {
      return null;
    }
  }

  SpecialistDto fetchSpecialist(String id) {
    try {
      return restClientBuilder
          .build()
          .get()
          .uri(userServiceUrl + "/api/v1/specialists/{id}", id)
          .retrieve()
          .body(SpecialistDto.class);
    } catch (RestClientException e) {
      return null;
    }
  }

  private void sendNewBookingEmail(SpecialistDto specialist, AddBookingDto booking) {
    try {
      restClientBuilder
          .build()
          .post()
          .uri(
              notificationServiceUrl
                  + "/api/notifications/send-booking-created?to={to}&name={name}&appointmentTime={at}",
              specialist.getEmail(),
              specialist.getName(),
              booking.getAppointmentTime().toString())
          .retrieve()
          .toBodilessEntity();
    } catch (RestClientException e) {
      // best-effort — email failure must not break booking creation
    }
  }

  private void sendBookingStatusEmail(String to, String name, String status) {
    try {
      restClientBuilder
          .build()
          .post()
          .uri(
              notificationServiceUrl
                  + "/api/notifications/send-booking-status?to={to}&name={name}&status={status}",
              to,
              name,
              status)
          .retrieve()
          .toBodilessEntity();
    } catch (RestClientException e) {
      // best-effort — email failure must not break status update
    }
  }
}
