package com.snapserve.userclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
  private String id;
  private String name;
  private String email;
  private String phone;
}
