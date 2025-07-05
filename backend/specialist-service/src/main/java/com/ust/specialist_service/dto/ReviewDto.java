package com.ust.specialist_service.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ReviewDto {
  private String reviewId;
  private String author;
  private String comment;
  private Integer rating;
  private LocalDate createdAt;
}
