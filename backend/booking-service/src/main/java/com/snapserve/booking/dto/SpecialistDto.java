package com.snapserve.booking.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistDto {
  private String id;
  private String name;
  private String email;
  private String phone;
  private String title;
  private List<String> services;
}
