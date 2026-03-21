package com.snapserve.booking.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.snapserve.booking.dto.request.BookingRequest;
import com.snapserve.booking.dto.request.UpdateBookingRequest;
import com.snapserve.booking.dto.response.BookingResponse;
import com.snapserve.booking.service.BookingService;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

  @Mock private BookingService bookingService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() throws Exception {
    Constructor<BookingController> constructor =
        BookingController.class.getDeclaredConstructor(BookingService.class);
    constructor.setAccessible(true);
    BookingController controller = constructor.newInstance(bookingService);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void createBookingUsesTrustedUserContextHeaders() throws Exception {
    String bookingId = new ObjectId().toString();
    LocalDateTime bookingDate = LocalDateTime.of(2026, 4, 1, 10, 0);
    BookingResponse response =
        new BookingResponse(
            bookingId,
            "customer-1",
            "specialist-1",
            bookingDate,
            "PENDING",
            "Fix kitchen sink",
            BigDecimal.valueOf(149.99),
            "Plumbing",
            Instant.now(),
            Instant.now());

    org.mockito.Mockito.when(
            bookingService.createBooking(
                eq("customer@snapserve.com"),
                eq("CUSTOMER"),
                eq(
                    new BookingRequest(
                        "spoofed-customer",
                        "specialist-1",
                        bookingDate,
                        "Fix kitchen sink",
                        BigDecimal.valueOf(149.99),
                        "Plumbing"))))
        .thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/bookings/")
                .header("X-User-Email", "customer@snapserve.com")
                .header("X-User-Roles", "CUSTOMER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"customerId":"spoofed-customer","specialistId":"specialist-1","bookingDate":"2026-04-01T10:00:00","notes":"Fix kitchen sink","price":149.99,"serviceType":"Plumbing"}
                    """))
        .andExpect(status().isCreated());

    verify(bookingService)
        .createBooking(
            "customer@snapserve.com",
            "CUSTOMER",
            new BookingRequest(
                "spoofed-customer",
                "specialist-1",
                bookingDate,
                "Fix kitchen sink",
                BigDecimal.valueOf(149.99),
                "Plumbing"));
  }

  @Test
  void getBookingByIdUsesTrustedUserContextHeaders() throws Exception {
    String bookingId = new ObjectId().toString();
    LocalDateTime bookingDate = LocalDateTime.of(2026, 4, 1, 10, 0);
    BookingResponse response =
        new BookingResponse(
            bookingId,
            "customer-1",
            "specialist-1",
            bookingDate,
            "PENDING",
            "Fix kitchen sink",
            BigDecimal.valueOf(149.99),
            "Plumbing",
            Instant.now(),
            Instant.now());

    org.mockito.Mockito.when(
            bookingService.getBookingById(
                eq(bookingId), eq("customer@snapserve.com"), eq("CUSTOMER")))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/api/v1/bookings/{id}", bookingId)
                .header("X-User-Email", "customer@snapserve.com")
                .header("X-User-Roles", "CUSTOMER"))
        .andExpect(status().isOk());

    verify(bookingService).getBookingById(bookingId, "customer@snapserve.com", "CUSTOMER");
  }

  @Test
  void updateBookingUsesTrustedUserContextHeaders() throws Exception {
    String bookingId = new ObjectId().toString();
    LocalDateTime bookingDate = LocalDateTime.of(2026, 4, 1, 10, 0);
    UpdateBookingRequest request = new UpdateBookingRequest(bookingDate, null, "Updated notes");
    BookingResponse response =
        new BookingResponse(
            bookingId,
            "customer-1",
            "specialist-1",
            bookingDate,
            "PENDING",
            "Updated notes",
            BigDecimal.valueOf(149.99),
            "Plumbing",
            Instant.now(),
            Instant.now());

    org.mockito.Mockito.when(
            bookingService.updateBooking(
                eq(bookingId), eq("customer@snapserve.com"), eq("CUSTOMER"), eq(request)))
        .thenReturn(response);

    mockMvc
        .perform(
            patch("/api/v1/bookings/{id}", bookingId)
                .header("X-User-Email", "customer@snapserve.com")
                .header("X-User-Roles", "CUSTOMER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"bookingDate":"2026-04-01T10:00:00","notes":"Updated notes"}
                    """))
        .andExpect(status().isOk());

    verify(bookingService).updateBooking(bookingId, "customer@snapserve.com", "CUSTOMER", request);
  }

  @Test
  void cancelBookingUsesTrustedUserContextHeaders() throws Exception {
    String bookingId = new ObjectId().toString();
    LocalDateTime bookingDate = LocalDateTime.of(2026, 4, 1, 10, 0);
    BookingResponse response =
        new BookingResponse(
            bookingId,
            "customer-1",
            "specialist-1",
            bookingDate,
            "CANCELLED",
            "Fix kitchen sink",
            BigDecimal.valueOf(149.99),
            "Plumbing",
            Instant.now(),
            Instant.now());

    org.mockito.Mockito.when(
            bookingService.updateBooking(
                eq(bookingId),
                eq("customer@snapserve.com"),
                eq("CUSTOMER"),
                eq(new UpdateBookingRequest(null, "CANCELLED", null))))
        .thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/bookings/{id}/cancel", bookingId)
                .header("X-User-Email", "customer@snapserve.com")
                .header("X-User-Roles", "CUSTOMER"))
        .andExpect(status().isOk());

    verify(bookingService)
        .updateBooking(
            bookingId,
            "customer@snapserve.com",
            "CUSTOMER",
            new UpdateBookingRequest(null, "CANCELLED", null));
  }

  @Test
  void deleteBookingUsesTrustedUserContextHeaders() throws Exception {
    String bookingId = new ObjectId().toString();

    mockMvc
        .perform(
            delete("/api/v1/bookings/{id}", bookingId)
                .header("X-User-Email", "customer@snapserve.com")
                .header("X-User-Roles", "CUSTOMER"))
        .andExpect(status().isNoContent());

    verify(bookingService).deleteBooking(bookingId, "customer@snapserve.com", "CUSTOMER");
  }
}
