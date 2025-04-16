package com.snapserve.userservice.mapper;

import com.snapserve.userservice.dto.CustomerRequest;
import com.snapserve.userservice.dto.CustomerResponse;
import com.snapserve.userservice.model.Customer;

public class CustomerMapper {

    public static Customer toEntity(CustomerRequest dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setAddress(dto.getAddress());
        customer.setProfilePic(dto.getProfilePic());
        customer.setDob(dto.getDob());
        customer.setGender(dto.getGender());

        return customer;
    }

    public static CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId().toHexString())
                .fullName(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .profilePic(customer.getProfilePic())
                .dob(customer.getDob())
                .gender(customer.getGender())
                .build();
    }
}
