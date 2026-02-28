package com.snapserve.booking.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
  private String id;
  private String name;
  private String email;
  private String phone;
  private String gender;
  private LocalDate dob;
  private String address;
  private String profilePictureUrl;
}
