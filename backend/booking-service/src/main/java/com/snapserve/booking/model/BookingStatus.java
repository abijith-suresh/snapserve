package com.snapserve.booking.model;

import com.snapserve.common.exception.BadRequestException;
import java.util.Locale;

public enum BookingStatus {
  PENDING,
  CONFIRMED,
  CANCELLED,
  COMPLETED;

  public static BookingStatus from(String value) {
    try {
      return BookingStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    } catch (RuntimeException ex) {
      throw new BadRequestException(
          "Invalid status. Must be one of: PENDING, CONFIRMED, CANCELLED, COMPLETED");
    }
  }

  public boolean canTransitionTo(BookingStatus nextStatus) {
    if (nextStatus == null || nextStatus == this) {
      return true;
    }

    return switch (this) {
      case PENDING -> nextStatus == CONFIRMED || nextStatus == CANCELLED;
      case CONFIRMED -> nextStatus == COMPLETED || nextStatus == CANCELLED;
      case CANCELLED, COMPLETED -> false;
    };
  }
}
