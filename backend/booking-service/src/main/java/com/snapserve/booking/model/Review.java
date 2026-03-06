package com.snapserve.booking.model;

import com.snapserve.common.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "reviews")
@CompoundIndex(name = "booking_idx", def = "{'bookingId': 1}", unique = true)
@CompoundIndex(name = "specialist_rating_idx", def = "{'specialistId': 1, 'rating': 1}")
public class Review extends Auditable {

  @Id private ObjectId id;

  @Indexed private String bookingId;

  @Indexed private String customerId;

  @Indexed private String specialistId;

  private Integer rating; // 1-5

  private String comment;

  @Version private Long version;
}
