package com.snapserve.booking.model;

import com.snapserve.common.model.Auditable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Document(collection = "bookings")
@CompoundIndex(name = "customer_status_idx", def = "{'customerId': 1, 'status': 1}")
@CompoundIndex(name = "specialist_status_idx", def = "{'specialistId': 1, 'status': 1}")
@CompoundIndex(name = "booking_date_idx", def = "{'bookingDate': 1}")
public class Booking extends Auditable {

  @Id private ObjectId id;

  @Indexed private String customerId;

  @Indexed private String specialistId;

  private LocalDateTime bookingDate;

  private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED

  private String notes;

  private BigDecimal price;

  private String serviceType;

  @Version private Long version;
}
