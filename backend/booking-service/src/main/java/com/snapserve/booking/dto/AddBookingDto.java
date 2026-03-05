package com.snapserve.booking.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBookingDto {
  private String customerId;
  private String specialistId;

  private LocalDateTime appointmentTime;
  private String service;
  private String status;
}
