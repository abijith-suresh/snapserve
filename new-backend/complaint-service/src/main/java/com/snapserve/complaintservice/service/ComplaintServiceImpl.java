package com.snapserve.complaintservice.service;

import com.snapserve.complaintservice.dto.ComplaintRequest;
import com.snapserve.complaintservice.dto.ComplaintResponse;
import com.snapserve.complaintservice.mapper.ComplaintMapper;
import com.snapserve.complaintservice.model.Complaint;
import com.snapserve.complaintservice.model.Status;
import com.snapserve.complaintservice.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintMapper complaintMapper;

    @Override
    public ComplaintResponse createComplaint(ComplaintRequest complaintRequest) {
        Complaint complaint = complaintMapper.toComplaintEntity(complaintRequest);
        Complaint savedComplaint = complaintRepository.save(complaint);
        return complaintMapper.toComplaintResponse(savedComplaint);
    }

    @Override
    public Optional<ComplaintResponse> getComplaintById(String id) {
        Optional<Complaint> complaint = complaintRepository.findById(id);
        return complaint.map(complaintMapper::toComplaintResponse);
    }

    @Override
    public List<ComplaintResponse> getComplaintsByBookingId(String bookingId) {
        List<Complaint> complaints = complaintRepository.findByBookingId(bookingId);
        return complaints.stream()
                .map(complaintMapper::toComplaintResponse)
                .toList();
    }

    @Override
    public List<ComplaintResponse> getComplaintsByCustomerEmail(String email) {
        List<Complaint> complaints = complaintRepository.findByEmail(email);
        return complaints.stream()
                .map(complaintMapper::toComplaintResponse)
                .toList();
    }

    @Override
    public Optional<ComplaintResponse> resolveComplaint(String id) {
        Optional<Complaint> complaint = complaintRepository.findById(id);
        if (complaint.isPresent()) {
            Complaint c = complaint.get();
            c.setStatus(Status.RESOLVED);
            Complaint savedComplaint = complaintRepository.save(c);
            return Optional.of(complaintMapper.toComplaintResponse(savedComplaint));
        }
        return Optional.empty();
    }

}