package com.snapserve.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class SpecialistRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String address;

    private byte[] profilePic;

    @NotBlank
    private LocalDate dob;

    @NotBlank
    private String gender;

    @NotBlank
    private String jobTitle;

    private String bio;

    @Positive
    private Integer yearsOfExperience;


    private List<@NotBlank String> certifications;
    private List<@NotBlank String> servicesOffered;

    @Positive
    private Double hourlyRate;

    @NotNull
    private Boolean isAvailable;
}
