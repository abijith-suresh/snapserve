package com.snapserve.userservice.repository;

import com.snapserve.userservice.model.Admin;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends UserRepository<Admin> {
}
