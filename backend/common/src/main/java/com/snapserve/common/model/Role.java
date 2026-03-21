package com.snapserve.common.model;

import com.snapserve.common.exception.BadRequestException;
import java.util.Locale;

public enum Role {
  CUSTOMER,
  SPECIALIST;

  public static Role from(String value) {
    try {
      return Role.valueOf(value.trim().toUpperCase(Locale.ROOT));
    } catch (RuntimeException ex) {
      throw new BadRequestException("Role must be one of: CUSTOMER, SPECIALIST.");
    }
  }
}
