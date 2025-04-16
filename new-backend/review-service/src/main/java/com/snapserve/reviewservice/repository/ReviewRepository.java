package com.snapserve.reviewservice.repository;

import com.snapserve.reviewservice.model.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, ObjectId> {
    List<Review> findBySpecialistId(String specialistId);
    List<Review> findByCustomerId(String customerId);
}