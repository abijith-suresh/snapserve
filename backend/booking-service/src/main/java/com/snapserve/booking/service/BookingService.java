package com.snapserve.booking.service;

import com.snapserve.booking.dto.AddBookingDto;
import com.snapserve.booking.dto.BookingResponseDto;
import com.snapserve.booking.model.Booking;
import com.snapserve.booking.repo.BookingRepository;
import com.snapserve.userclient.client.UserServiceClient;
import com.snapserve.userclient.dto.customer.CustomerResponse;
import com.snapserve.userclient.dto.specialist.SpecialistResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

  @Autowired private BookingRepository bookingRepo;

  @Autowired private UserServiceClient userServiceClient;

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
    CustomerResponse customer = fetchCustomer(booking.getCustomerId().toString());
    SpecialistResponse specialist = fetchSpecialist(booking.getSpecialistId().toString());
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

  CustomerResponse fetchCustomer(String id) {
    try {
      return userServiceClient.getCustomerById(id);
    } catch (Exception e) {
      return null;
    }
  }

  SpecialistResponse fetchSpecialist(String id) {
    try {
      return userServiceClient.getSpecialistById(id);
    } catch (Exception e) {
      return null;
    }
  }
}
