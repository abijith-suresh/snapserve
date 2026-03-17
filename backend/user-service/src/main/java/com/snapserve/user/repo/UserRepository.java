package com.snapserve.user.repo;

import com.snapserve.common.model.Role;
import com.snapserve.user.model.UserEntity;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, ObjectId> {

  Optional<UserEntity> findByEmail(String email);

  boolean existsByEmail(String email);

  List<UserEntity> findByRole(Role role);

  Page<UserEntity> findByRole(Role role, Pageable pageable);

  Optional<UserEntity> findByIdAndRole(ObjectId id, Role role);

  List<UserEntity> findByRoleAndServicesContaining(Role role, String service);

  Page<UserEntity> findByRoleAndServicesContaining(Role role, String service, Pageable pageable);

  List<UserEntity> findByPhone(String phone);

  List<UserEntity> findByRoleAndPhone(Role role, String phone);
}
