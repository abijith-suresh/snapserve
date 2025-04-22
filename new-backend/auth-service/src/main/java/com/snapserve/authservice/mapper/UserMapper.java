package com.snapserve.authservice.mapper;

import com.snapserve.authservice.client.dto.AdminCreateRequest;
import com.snapserve.authservice.client.dto.CustomerCreateRequest;
import com.snapserve.authservice.client.dto.SpecialistCreateRequest;
import com.snapserve.authservice.dto.request.RegisterRequest;

import java.time.LocalDate;
import java.util.Collections;

public class UserMapper {

    public static AdminCreateRequest toAdminCreateRequest(RegisterRequest request) {
        return AdminCreateRequest.builder()
                .firstName("AdminFirstName")
                .lastName("AdminLastName")
                .email(request.getEmail())
                .phoneNumber("000-000-0000")
                .address("Admin Address")
                .dob(LocalDate.now())
                .gender("Unspecified")
                .roleDescription("Administrator user")
                .build();
    }

    public static CustomerCreateRequest toCustomerCreateRequest(RegisterRequest request) {
        return CustomerCreateRequest.builder()
                .firstName("CustomerFirstName")
                .lastName("CustomerLastName")
                .email(request.getEmail())
                .phoneNumber("000-000-0000")
                .address("Customer Address")
                .dob(LocalDate.now())
                .gender("Unspecified")
                .build();
    }

    public static SpecialistCreateRequest toSpecialistCreateRequest(RegisterRequest request) {
        return SpecialistCreateRequest.builder()
                .firstName("SpecialistFirstName")
                .lastName("SpecialistLastName")
                .email(request.getEmail())
                .phoneNumber("000-000-0000")
                .address("Specialist Address")
                .dob(LocalDate.now())
                .gender("Unspecified")
                .jobTitle("Specialist")
                .bio("Bio of the specialist.")
                .yearsOfExperience(0)
                .certifications(Collections.emptyList())
                .servicesOffered(Collections.emptyList())
                .hourlyRate(0.0)
                .isAvailable(true)
                .build();
    }
}