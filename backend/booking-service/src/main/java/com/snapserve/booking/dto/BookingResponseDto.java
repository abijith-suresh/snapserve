package com.snapserve.booking.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {

  private String bookingId;
  private CustomerDto customer;
  private SpecialistDto specialist;
  private LocalDateTime appointmentTime;
  private String service;
  private String status;
}
