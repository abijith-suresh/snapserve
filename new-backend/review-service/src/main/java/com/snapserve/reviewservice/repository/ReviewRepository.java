package com.snapserve.reviewservice.repository;

import com.snapserve.reviewservice.model.Review;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, ObjectId> {
    Page<Review> findBySpecialistId(String specialistId, Pageable pageable);
    List<Review> findBySpecialistId(String specialistId);
    Page<Review> findByCustomerId(String customerId, Pageable pageable);
}