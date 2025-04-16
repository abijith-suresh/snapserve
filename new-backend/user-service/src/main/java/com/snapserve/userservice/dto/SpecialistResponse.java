package com.snapserve.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistResponse {
    private String id;
    private String fullName;
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
