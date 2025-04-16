package com.snapserve.reviewservice.service;

import com.snapserve.reviewservice.dto.RatingSummaryResponse;
import com.snapserve.reviewservice.dto.ReviewRequest;
import com.snapserve.reviewservice.dto.ReviewResponse;
import com.snapserve.reviewservice.mapper.ReviewMapper;
import com.snapserve.reviewservice.model.Review;
import com.snapserve.reviewservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new NoSuchElementException("Review not found"));
        return mapper.toResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsBySpecialist(String specialistId) {
        return reviewRepository.findBySpecialistId(specialistId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByCustomer(String customerId) {
        return reviewRepository.findByCustomerId(customerId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(String id) {
        reviewRepository.deleteById(new ObjectId(id));
    }

    @Override
    public void markHelpful(String id) {
        Review review = reviewRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchElementException("Review not found"));
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    @Override
    public RatingSummaryResponse getRatingSummaryForSpecialist(String specialistId) {
        List<Review> reviews = reviewRepository.findBySpecialistId(specialistId);
        if (reviews.isEmpty()) {
            throw new NoSuchElementException("No reviews found for specialist");
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
