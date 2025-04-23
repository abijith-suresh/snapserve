package com.snapserve.authservice.repository;

import com.snapserve.authservice.model.AuthUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuthUserRepository extends MongoRepository<AuthUser,String> {
    Optional<AuthUser> findByEmail(String email);
}
