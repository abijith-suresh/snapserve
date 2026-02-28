package com.snapserve.user.repo;

import com.snapserve.user.model.Admin;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdminRepository extends MongoRepository<Admin, ObjectId> {

  Optional<Admin> findByEmail(String email);
}
