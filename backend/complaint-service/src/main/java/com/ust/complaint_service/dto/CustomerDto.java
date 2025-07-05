package com.ust.complaint_service.dto;

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
  private String phoneNumber;
  private String gender;
  private LocalDate dob;
  private String address;
  private String profilePictureUrl;
}
