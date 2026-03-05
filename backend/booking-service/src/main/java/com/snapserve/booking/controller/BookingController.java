package com.snapserve.booking.controller;

import com.snapserve.booking.dto.AddBookingDto;
import com.snapserve.booking.dto.BookingResponseDto;
import com.snapserve.booking.model.Booking;
import com.snapserve.booking.service.BookingService;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

  @Autowired private BookingService bookingService;

  @PostMapping
  public ResponseEntity<String> createBooking(@RequestBody AddBookingDto booking) {
    String result = bookingService.createBooking(booking);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @GetMapping
  public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
    return ResponseEntity.ok(bookingService.getAllBookings());
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable String id) {
    BookingResponseDto booking = bookingService.getBookingById(new ObjectId(id));
    if (booking == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(booking);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Booking> updateBooking(
      @PathVariable String id, @RequestBody Booking bookingDetails) {
    Booking updated = bookingService.updateBooking(new ObjectId(id), bookingDetails);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBooking(@PathVariable String id) {
    bookingService.deleteBooking(new ObjectId(id));
    return ResponseEntity.noContent().build();
  }
}
