package com.snapserve.booking.repo;

import com.snapserve.booking.model.Review;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> {

  List<Review> findByCustomerId(ObjectId customerId);

  List<Review> findBySpecialistId(ObjectId specialistId);
}
