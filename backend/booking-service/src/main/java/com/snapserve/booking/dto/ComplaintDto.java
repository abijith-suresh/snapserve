package com.snapserve.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintDto {
  private String id;
  private String name;
  private String email;
  private String message;
  private BookingResponseDto booking;
  private String attachments;
}
