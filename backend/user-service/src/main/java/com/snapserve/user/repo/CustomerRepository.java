package com.snapserve.user.repo;

import com.snapserve.user.model.Customer;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, ObjectId> {

  Optional<Customer> findByEmail(String email);
}
