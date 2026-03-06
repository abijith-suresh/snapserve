package com.snapserve.user.model;

import com.snapserve.common.model.Auditable;
import com.snapserve.common.model.Role;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Document(collection = "users")
@CompoundIndex(name = "user_role_index", def = "{'role': 1}")
@CompoundIndex(name = "services_index", def = "{'services': 1}")
public class UserEntity extends Auditable {

  @Id private ObjectId id;

  @Indexed(unique = true)
  private String email;

  private String name;

  @Indexed private Role role;

  private String phone;

  private String address;
  private String preferredPaymentMethod;

  private String title;
  private List<String> services;
  private BigDecimal hourlyRate;
  private Boolean verified;
}
