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

  @Autowired private BookingRepository bookingRepo;

  @Autowired private RestClient.Builder restClientBuilder;

  private Booking dtoToModel(AddBookingDto bookingDto) {
    Booking booking = new Booking();
    booking.setCustomerId(new ObjectId(bookingDto.getCustomerId()));
    booking.setSpecialistId(new ObjectId(bookingDto.getSpecialistId()));
    booking.setAppointmentTime(bookingDto.getAppointmentTime());
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
        booking.getAppointmentTime(),
        booking.getService(),
        booking.getStatus());
  }

  public String createBooking(AddBookingDto bookingDto) {
    Booking newBooking = dtoToModel(bookingDto);
    bookingRepo.save(newBooking);
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
}
