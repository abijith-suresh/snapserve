package com.snapserve.booking.repo;

import com.snapserve.booking.model.Booking;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends MongoRepository<Booking, ObjectId> {

  List<Booking> findByCustomerId(ObjectId customerId);

  List<Booking> findBySpecialistId(ObjectId specialistId);
}
