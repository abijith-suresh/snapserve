package com.snapserve.booking.repository;

import com.snapserve.booking.model.Booking;
import java.time.LocalDateTime;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends MongoRepository<Booking, ObjectId> {

  Page<Booking> findByCustomerId(String customerId, Pageable pageable);

  Page<Booking> findBySpecialistId(String specialistId, Pageable pageable);

  Page<Booking> findByCustomerIdAndStatus(String customerId, String status, Pageable pageable);

  Page<Booking> findBySpecialistIdAndStatus(String specialistId, String status, Pageable pageable);

  List<Booking> findByBookingDateBetween(LocalDateTime start, LocalDateTime end);

  boolean existsByIdAndCustomerId(ObjectId id, String customerId);

  boolean existsByIdAndSpecialistId(ObjectId id, String specialistId);

  @Query(
      "{ 'specialistId': ?0, 'bookingDate': { $gte: ?1, $lte: ?2 }, 'status': { $nin: ['CANCELLED'] } }")
  List<Booking> findConflictingBookings(
      String specialistId, LocalDateTime start, LocalDateTime end);
}
