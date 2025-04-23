package com.snapserve.authservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private byte[] profilePic;
    private LocalDate dob;
    private String gender;
}