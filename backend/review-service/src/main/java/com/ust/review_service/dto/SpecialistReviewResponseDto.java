package com.ust.review_service.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistReviewResponseDto {
  private String reviewId;
  private String author;
  private String comment;
  private Integer rating;
  private LocalDate createdAt;
}
