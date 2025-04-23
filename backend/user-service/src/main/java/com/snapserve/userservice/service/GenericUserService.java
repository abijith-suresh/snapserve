package com.snapserve.userservice.service;

import com.snapserve.userservice.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface GenericUserService<ENTITY, REQUEST, RESPONSE> {
    RESPONSE createUser(REQUEST request);
    RESPONSE getUserById(String id);
    PagedResponse<RESPONSE> getAllUsers(Pageable pageable);
    RESPONSE updateUser(String id, REQUEST request);
    void deleteUser(String id);
}