package com.ust.customer_service.dto;

import java.util.Date;
import lombok.Data;

@Data
public class ReviewDto {

  private String customer_id;
  private String specialist_id;
  private Integer rating;
  private String comment;
  private Date createdAt;
}
