package com.snapserve.booking.controller;

import com.snapserve.booking.dto.ComplaintDto;
import com.snapserve.booking.model.Complaint;
import com.snapserve.booking.service.ComplaintService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/complaints")
public class ComplaintController {

  @Autowired private ComplaintService complaintService;

  @PostMapping
  public ResponseEntity<ComplaintDto> submitComplaint(@RequestBody Complaint complaint) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(complaintService.submitComplaint(complaint));
  }

  @GetMapping
  public ResponseEntity<List<ComplaintDto>> getAllComplaints() {
    return ResponseEntity.ok(complaintService.getAllComplaints());
  }
}
