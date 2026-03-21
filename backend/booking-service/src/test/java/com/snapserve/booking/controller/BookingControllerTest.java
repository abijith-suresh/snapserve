package com.snapserve.booking.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.snapserve.booking.dto.request.BookingRequest;
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
}
