package com.snapserve.complaintservice.service;

import com.snapserve.complaintservice.dto.ComplaintRequest;
import com.snapserve.complaintservice.dto.ComplaintResponse;

import java.util.List;
import java.util.Optional;

public interface ComplaintService {

    ComplaintResponse createComplaint(ComplaintRequest complaintRequest);
    Optional<ComplaintResponse> getComplaintById(String id);
    List<ComplaintResponse> getComplaintsByBookingId(String bookingId);
    List<ComplaintResponse> getComplaintsByCustomerEmail(String email);
    Optional<ComplaintResponse> resolveComplaint(String id);

}