package com.snapserve.booking.dto;

import com.snapserve.userclient.dto.customer.CustomerResponse;
import com.snapserve.userclient.dto.specialist.SpecialistResponse;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {

  private String bookingId;
  private CustomerResponse customer;
  private SpecialistResponse specialist;
  private LocalDateTime appointmentTime;
  private String service;
  private String status;
}
