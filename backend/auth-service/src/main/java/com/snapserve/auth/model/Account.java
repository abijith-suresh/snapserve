package com.snapserve.auth.model;

import com.snapserve.common.model.Auditable;
import com.snapserve.common.model.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Document(collection = "account")
public class Account extends Auditable {

  @Id private ObjectId id;

  @Indexed(unique = true)
  private String email;

  private String password;

  private Role role;

  private boolean enabled = true;

  private boolean locked = false;

  private int failedLoginAttempts = 0;

  private Instant lastFailedLoginAt;
}
