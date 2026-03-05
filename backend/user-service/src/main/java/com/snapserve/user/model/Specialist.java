package com.snapserve.user.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "specialist")
public class Specialist {

  @Id private ObjectId id;

  private String name;
  private String email;
  private String phone;
  private String title;
  private List<String> services;
}
