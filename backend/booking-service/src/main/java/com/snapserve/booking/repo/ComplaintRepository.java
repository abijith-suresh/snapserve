package com.snapserve.booking.repo;

import com.snapserve.booking.model.Complaint;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends MongoRepository<Complaint, ObjectId> {}
