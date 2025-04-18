package com.snapserve.userservice.service;

import java.util.List;

public interface GenericUserService<ENTITY, REQUEST, RESPONSE> {
    RESPONSE createUser(REQUEST request);
    RESPONSE getUserById(String id);
    List<RESPONSE> getAllUsers();
    RESPONSE updateUser(String id, REQUEST request);
    void deleteUser(String id);
}