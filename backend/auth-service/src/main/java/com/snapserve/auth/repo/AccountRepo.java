package com.snapserve.auth.repo;

import com.snapserve.auth.model.Account;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends MongoRepository<Account, ObjectId> {
  Optional<Account> findByEmail(String email);
}
