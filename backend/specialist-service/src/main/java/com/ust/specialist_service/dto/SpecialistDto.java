package com.ust.specialist_service.dto;

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
  private String phoneNumber;
  private String title;
  private String bio;
  private String price;
  private double rating;
  private String profileImage;
  private List<String> services;
  private List<String> photos;
  private int experience;
  private String address;
  private String status;
}
