package com.snapserve.userservice.mapper;

import com.snapserve.userservice.dto.UserDetailResponse;
import com.snapserve.userservice.model.Customer;
import com.snapserve.userservice.model.Specialist;
import com.snapserve.userservice.model.User;

public class UserDetailMapper {

    public static UserDetailResponse fromUser(User user) {
        UserDetailResponse response = new UserDetailResponse();
        response.setId(user.getId().toHexString());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setDob(user.getDob());
        response.setGender(user.getGender());
        response.setActive(user.getActive());
        response.setVerified(user.getVerified());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setLastLogin(user.getLastLogin());
        return response;
    }

    public static UserDetailResponse fromCustomer(Customer customer) {
        return fromUser(customer);
    }

    public static UserDetailResponse fromSpecialist(Specialist specialist) {
        UserDetailResponse response = fromUser(specialist);
        response.setJobTitle(specialist.getJobTitle());
        response.setBio(specialist.getBio());
        response.setYearsOfExperience(specialist.getYearsOfExperience());
        response.setCertifications(specialist.getCertifications());
        response.setServicesOffered(specialist.getServicesOffered());
        response.setRating(specialist.getRating());
        response.setHourlyRate(specialist.getHourlyRate());
        return response;
    }

}
