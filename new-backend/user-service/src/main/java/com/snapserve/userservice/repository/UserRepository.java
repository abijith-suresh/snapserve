package com.snapserve.userservice.repository;

import com.snapserve.userservice.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserRepository<T extends User> extends MongoRepository<T, ObjectId> {

}