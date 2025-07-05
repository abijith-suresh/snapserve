package com.ust.specialist_service.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
  private String bookingId;
  private CustomerDto customer;
  private SpecialistDto specialist;
  private LocalDateTime bookingDate;
  private LocalDateTime appointmentTime;
  private String service;
  private String status;
  private String price;
}
