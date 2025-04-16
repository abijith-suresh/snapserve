package com.snapserve.complaintservice.repository;

import com.snapserve.complaintservice.model.Complaint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends MongoRepository<Complaint, String> {

    Optional<Complaint> findById(String id);

    List<Complaint> findByBookingId(String bookingId);

    List<Complaint> findByEmail(String email);
}