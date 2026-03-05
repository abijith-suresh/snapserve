package com.snapserve.booking.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "booking")
public class Booking {

  @Id private ObjectId id;

  private ObjectId customerId;
  private ObjectId specialistId;

  private LocalDateTime appointmentTime;
  private String service;
  private String status;
}
