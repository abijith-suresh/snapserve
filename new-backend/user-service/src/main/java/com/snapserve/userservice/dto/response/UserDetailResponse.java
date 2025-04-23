package com.snapserve.userservice.dto.response;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserDetailResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate dob;
    private String gender;
    private Boolean active;
    private Boolean verified;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLogin;

    // Specialist-specific fields
    private String jobTitle;
    private String bio;
    private Integer yearsOfExperience;
    private List<String> certifications;
    private List<String> servicesOffered;
    private Double rating;
    private Double hourlyRate;
}