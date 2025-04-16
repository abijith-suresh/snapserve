package com.snapserve.complaintservice.controller;

import com.snapserve.complaintservice.dto.ComplaintRequest;
import com.snapserve.complaintservice.dto.ComplaintResponse;
import com.snapserve.complaintservice.service.ComplaintService;
import com.snapserve.complaintservice.dto.ApiResponse;
import com.snapserve.complaintservice.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(@RequestBody ComplaintRequest request) {
        return ResponseBuilder.created(complaintService.createComplaint(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaint(@PathVariable String id) {
        return complaintService.getComplaintById(id)
                .map(ResponseBuilder::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getComplaintsByBookingId(@PathVariable String bookingId) {
        return ResponseBuilder.ok(complaintService.getComplaintsByBookingId(bookingId));
    }

    @GetMapping("/customer/{email}")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getComplaintsByCustomerEmail(@PathVariable String email) {
        return ResponseBuilder.ok(complaintService.getComplaintsByCustomerEmail(email));
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<ComplaintResponse>> resolveComplaint(@PathVariable String id) {
        return complaintService.resolveComplaint(id)
                .map(ResponseBuilder::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
