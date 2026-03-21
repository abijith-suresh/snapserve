package com.snapserve.common.mongo;

import com.snapserve.common.exception.BadRequestException;
import org.bson.types.ObjectId;

public final class ObjectIdParser {

  private ObjectIdParser() {}

  public static ObjectId parse(String id, String resourceName) {
    if (!ObjectId.isValid(id)) {
      throw new BadRequestException("Invalid " + resourceName + " ID format.");
    }

    return new ObjectId(id);
  }
}
