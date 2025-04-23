package com.snapserve.reviewservice.service;

import com.snapserve.reviewservice.dto.RatingSummaryResponse;
import com.snapserve.reviewservice.dto.ReviewRequest;
import com.snapserve.reviewservice.dto.ReviewResponse;
import com.snapserve.reviewservice.exception.ReviewNotFoundException;
import com.snapserve.reviewservice.mapper.ReviewMapper;
import com.snapserve.reviewservice.model.Review;
import com.snapserve.reviewservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper mapper;

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        Review review = mapper.toEntity(request);
        Review saved = reviewRepository.save(review);
        return mapper.toResponse(saved);
    }

    @Override
    public ReviewResponse getReviewById(String id) {
        Review review = reviewRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        return mapper.toResponse(review);
    }

    @Override
    public Page<ReviewResponse> getReviewsBySpecialist(String specialistId, Pageable pageable) {
        return reviewRepository.findBySpecialistId(specialistId, pageable)
                .map(mapper::toResponse);
    }

    @Override
    public Page<ReviewResponse> getReviewsByCustomer(String customerId, Pageable pageable) {
        return reviewRepository.findByCustomerId(customerId, pageable)
                .map(mapper::toResponse);
    }

    @Override
    public void deleteReview(String id) {
        reviewRepository.deleteById(new ObjectId(id));
    }

    @Override
    public void markHelpful(String id) {
        Review review = reviewRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    @Override
    public RatingSummaryResponse getRatingSummaryForSpecialist(String specialistId) {
        List<Review> reviews = reviewRepository.findBySpecialistId(specialistId);
        if (reviews.isEmpty()) {
            throw new ReviewNotFoundException("No reviews found for specialist");
        }

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return RatingSummaryResponse.builder()
                .specialistId(specialistId)
                .averageRating(Math.round(average * 10.0) / 10.0)
                .totalReviews(reviews.size())
                .build();
    }

}
