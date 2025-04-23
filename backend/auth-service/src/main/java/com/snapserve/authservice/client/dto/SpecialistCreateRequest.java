package com.snapserve.authservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistCreateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private byte[] profilePic;
    private LocalDate dob;
    private String gender;
    private String jobTitle;
    private String bio;
    private Integer yearsOfExperience;
    private List<String> certifications;
    private List<String> servicesOffered;
    private Double hourlyRate;
    private Boolean isAvailable;
}