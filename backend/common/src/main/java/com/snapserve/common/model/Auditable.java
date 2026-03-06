package com.snapserve.common.model;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
public abstract class Auditable {
  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;
}
