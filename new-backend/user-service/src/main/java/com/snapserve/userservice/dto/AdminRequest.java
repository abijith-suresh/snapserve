package com.snapserve.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private byte[] profilePic;
    private LocalDate dob;
    private String gender;

    private String roleDescription;
}
