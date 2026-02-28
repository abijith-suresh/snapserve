package com.snapserve.user.repo;

import com.snapserve.user.model.Specialist;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpecialistRepository extends MongoRepository<Specialist, ObjectId> {

  Optional<Specialist> findByEmail(String email);

  List<Specialist> findByStatus(String status);
}
