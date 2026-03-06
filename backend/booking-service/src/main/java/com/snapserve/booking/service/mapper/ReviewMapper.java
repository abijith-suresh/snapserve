package com.snapserve.booking.service.mapper;

import com.snapserve.booking.dto.request.ReviewRequest;
import com.snapserve.booking.dto.response.ReviewResponse;
import com.snapserve.booking.model.Review;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "customerId", ignore = true)
  @Mapping(target = "specialistId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  Review toEntity(ReviewRequest request);

  @Mapping(target = "id", expression = "java(review.getId().toString())")
  ReviewResponse toResponse(Review review);

  List<ReviewResponse> toResponseList(List<Review> reviews);
}
