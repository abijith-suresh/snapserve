package com.snapserve.userservice.mapper;

import com.snapserve.userservice.dto.SpecialistRequest;
import com.snapserve.userservice.dto.SpecialistResponse;
import com.snapserve.userservice.model.Specialist;

public class SpecialistMapper {

    public static Specialist toEntity(SpecialistRequest dto) {
        Specialist specialist = new Specialist();
        specialist.setFirstName(dto.getFirstName());
        specialist.setLastName(dto.getLastName());
        specialist.setEmail(dto.getEmail());
        specialist.setPhoneNumber(dto.getPhoneNumber());
        specialist.setAddress(dto.getAddress());
        specialist.setProfilePic(dto.getProfilePic());

        specialist.setJobTitle(dto.getJobTitle());
        specialist.setBio(dto.getBio());
        specialist.setYearsOfExperience(dto.getYearsOfExperience());
        specialist.setCertifications(dto.getCertifications());
        specialist.setServicesOffered(dto.getServicesOffered());
        specialist.setHourlyRate(dto.getHourlyRate());
        specialist.setIsAvailable(dto.getIsAvailable());

        return specialist;
    }

    public static SpecialistResponse toResponse(Specialist specialist) {
        return SpecialistResponse.builder()
                .id(specialist.getId().toHexString())
                .fullName(specialist.getFirstName() + " " + specialist.getLastName())
                .email(specialist.getEmail())
                .phoneNumber(specialist.getPhoneNumber())
                .address(specialist.getAddress())
                .profilePic(specialist.getProfilePic())

                .jobTitle(specialist.getJobTitle())
                .bio(specialist.getBio())
                .yearsOfExperience(specialist.getYearsOfExperience())
                .certifications(specialist.getCertifications())
                .servicesOffered(specialist.getServicesOffered())
                .hourlyRate(specialist.getHourlyRate())
                .isAvailable(specialist.getIsAvailable())
                .build();
    }
}
