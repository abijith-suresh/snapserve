package com.snapserve.booking.repository;

import com.snapserve.booking.model.Review;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> {

  Page<Review> findBySpecialistId(String specialistId, Pageable pageable);

  Page<Review> findByCustomerId(String customerId, Pageable pageable);

  Optional<Review> findByBookingId(String bookingId);

  boolean existsByBookingId(String bookingId);

  @Aggregation(
      pipeline = {
        "{ $match: { 'specialistId': ?0 } }",
        "{ $group: { _id: null, avgRating: { $avg: '$rating' }, count: { $sum: 1 }, ratings: { $push: '$rating' } } }"
      })
  Optional<ReviewAggregationResult> calculateSpecialistStats(String specialistId);

  // Helper class for aggregation results
  class ReviewAggregationResult {
    private Double avgRating;
    private Long count;
    private List<Integer> ratings;

    public Double getAvgRating() {
      return avgRating;
    }

    public void setAvgRating(Double avgRating) {
      this.avgRating = avgRating;
    }

    public Long getCount() {
      return count;
    }

    public void setCount(Long count) {
      this.count = count;
    }

    public List<Integer> getRatings() {
      return ratings;
    }

    public void setRatings(List<Integer> ratings) {
      this.ratings = ratings;
    }
  }
}
