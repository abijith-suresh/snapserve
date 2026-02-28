package com.snapserve.booking.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistReviewResponseDto {
  private String author;
  private Integer rating;
  private String comment;
  private LocalDate createdAt;
}
