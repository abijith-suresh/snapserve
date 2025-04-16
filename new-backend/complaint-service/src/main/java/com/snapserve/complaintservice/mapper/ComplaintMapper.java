package com.snapserve.complaintservice.mapper;

import com.snapserve.complaintservice.dto.ComplaintRequest;
import com.snapserve.complaintservice.dto.ComplaintResponse;
import com.snapserve.complaintservice.model.Complaint;
import com.snapserve.complaintservice.model.Status;
import org.springframework.stereotype.Component;

@Component
public class ComplaintMapper {

    public ComplaintResponse toComplaintResponse(Complaint complaint) {
        return new ComplaintResponse(
                complaint.getId().toString(),
                complaint.getName(),
                complaint.getEmail(),
                complaint.getMessage(),
                complaint.getBookingId(),
                complaint.getAttachments(),
                complaint.getStatus().name()
        );
    }

    public Complaint toComplaintEntity(ComplaintRequest complaintRequest) {
        return new Complaint(
                null,
                complaintRequest.getName(),
                complaintRequest.getEmail(),
                complaintRequest.getMessage(),
                complaintRequest.getBookingId(),
                complaintRequest.getAttachments(),
                Status.NEW
        );
    }
}