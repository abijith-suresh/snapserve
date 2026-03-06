package com.snapserve.auth.repo;

import com.snapserve.auth.model.RefreshToken;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepo extends MongoRepository<RefreshToken, ObjectId> {
  Optional<RefreshToken> findByToken(String token);

  Optional<RefreshToken> findByUserIdAndRevokedFalse(ObjectId userId);

  void deleteByUserId(ObjectId userId);
}
