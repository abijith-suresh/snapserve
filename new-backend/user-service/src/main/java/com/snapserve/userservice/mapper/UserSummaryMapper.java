package com.snapserve.userservice.mapper;

import com.snapserve.userservice.dto.response.UserSummaryResponse;
import com.snapserve.userservice.model.Customer;
import com.snapserve.userservice.model.Specialist;

public class UserSummaryMapper {

    public static UserSummaryResponse fromCustomer(Customer customer) {
        return UserSummaryResponse.builder()
                .id(customer.getId().toHexString())
                .name(customer.getFirstName() + ' ' + customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .role("CUSTOMER")
                .profilePic(customer.getProfilePic())
                .build();
    }

    public static UserSummaryResponse fromSpecialist(Specialist specialist) {
        return UserSummaryResponse.builder()
                .id(specialist.getId().toHexString())
                .name(specialist.getFirstName() + ' ' + specialist.getLastName())
                .email(specialist.getEmail())
                .phoneNumber(specialist.getPhoneNumber())
                .role("SPECIALIST")
                .profilePic(specialist.getProfilePic())
                .build();
    }
}
