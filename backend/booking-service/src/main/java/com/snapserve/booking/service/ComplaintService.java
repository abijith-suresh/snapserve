package com.snapserve.booking.service;

import com.snapserve.booking.dto.BookingResponseDto;
import com.snapserve.booking.dto.ComplaintDto;
import com.snapserve.booking.model.Complaint;
import com.snapserve.booking.repo.ComplaintRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplaintService {

  @Autowired private ComplaintRepository complaintRepo;

  @Autowired private BookingService bookingService;

  private ComplaintDto toDto(Complaint complaint) {
    BookingResponseDto booking = null;
    if (complaint.getBookingId() != null && !complaint.getBookingId().isBlank()) {
      try {
        booking = bookingService.getBookingById(new ObjectId(complaint.getBookingId()));
      } catch (IllegalArgumentException e) {
        // invalid ObjectId — leave booking null
      }
    }
    return new ComplaintDto(
        complaint.getId() != null ? complaint.getId().toString() : null,
        complaint.getName(),
        complaint.getEmail(),
        complaint.getMessage(),
        booking,
        complaint.getAttachments());
  }

  public ComplaintDto submitComplaint(Complaint complaint) {
    return toDto(complaintRepo.save(complaint));
  }

  public List<ComplaintDto> getAllComplaints() {
    return complaintRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
  }
}
