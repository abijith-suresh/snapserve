package com.snapserve.booking.service;

import com.snapserve.booking.dto.ReviewDto;
import com.snapserve.booking.dto.SpecialistReviewResponseDto;
import com.snapserve.booking.model.Review;
import com.snapserve.booking.repo.ReviewRepository;
import com.snapserve.userclient.dto.CustomerDto;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

  @Autowired private ReviewRepository reviewRepo;

  @Autowired private BookingService bookingService;

  private ReviewDto toDto(Review review) {
    ReviewDto dto = new ReviewDto();
    dto.setCustomerId(review.getCustomerId().toString());
    dto.setSpecialistId(review.getSpecialistId().toString());
    dto.setRating(review.getRating());
    dto.setComment(review.getComment());
    dto.setCreatedAt(review.getCreatedAt());
    return dto;
  }

  public List<ReviewDto> findAllReviews() {
    return reviewRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
  }

  public Review findReviewById(ObjectId id) {
    return reviewRepo.findById(id).orElse(null);
  }

  public ReviewDto createReview(ReviewDto dto) {
    Review review = new Review();
    review.setCustomerId(new ObjectId(dto.getCustomerId()));
    review.setSpecialistId(new ObjectId(dto.getSpecialistId()));
    review.setRating(dto.getRating());
    review.setComment(dto.getComment());
    review.setCreatedAt(dto.getCreatedAt());
    return toDto(reviewRepo.save(review));
  }

  public Review updateReview(ObjectId id, Review reviewDetails) {
    reviewDetails.setId(id);
    return reviewRepo.save(reviewDetails);
  }

  public void deleteReviewById(ObjectId id) {
    reviewRepo.deleteById(id);
  }

  public List<ReviewDto> getReviewsByCustomer(ObjectId customerId) {
    return reviewRepo.findByCustomerId(customerId).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  public List<SpecialistReviewResponseDto> getReviewsForSpecialist(ObjectId specialistId) {
    return reviewRepo.findBySpecialistId(specialistId).stream()
        .map(
            review -> {
              CustomerDto customer =
                  bookingService.fetchCustomer(review.getCustomerId().toString());
              String authorName = customer != null ? customer.getName() : "Unknown";
              return new SpecialistReviewResponseDto(
                  authorName, review.getRating(), review.getComment(), review.getCreatedAt());
            })
        .collect(Collectors.toList());
  }
}
