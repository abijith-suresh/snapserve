package com.snapserve.userservice.mapper;

import com.snapserve.userservice.dto.AdminRequest;
import com.snapserve.userservice.dto.AdminResponse;
import com.snapserve.userservice.model.Admin;

public class AdminMapper {

    public static Admin toEntity(AdminRequest dto) {
        Admin admin = new Admin();
        admin.setFirstName(dto.getFirstName());
        admin.setLastName(dto.getLastName());
        admin.setEmail(dto.getEmail());
        admin.setPhoneNumber(dto.getPhoneNumber());
        admin.setAddress(dto.getAddress());
        admin.setProfilePic(dto.getProfilePic());
        admin.setRoleDescription(dto.getRoleDescription());
        admin.setDob(dto.getDob());
        admin.setGender(dto.getGender());

        return admin;
    }

    public static AdminResponse toResponse(Admin admin) {
        return AdminResponse.builder()
                .id(admin.getId().toHexString())
                .fullName(admin.getFirstName() + " " + admin.getLastName())
                .email(admin.getEmail())
                .phoneNumber(admin.getPhoneNumber())
                .address(admin.getAddress())
                .profilePic(admin.getProfilePic())
                .roleDescription(admin.getRoleDescription())
                .dob(admin.getDob())
                .gender(admin.getGender())
                .build();
    }
}
