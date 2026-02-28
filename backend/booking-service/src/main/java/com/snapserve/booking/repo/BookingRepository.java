package com.snapserve.booking.repo;

import com.snapserve.booking.entity.Booking;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BookingRepository extends ReactiveMongoRepository<Booking, ObjectId> {

  Flux<Booking> findByCustomerId(ObjectId customerId);

  Flux<Booking> findBySpecialistId(ObjectId specialistId);
}
