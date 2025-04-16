package com.snapserve.userservice.service;

import java.util.List;

public interface GenericUserService<T> {
    T createUser(T user);
    T getUserById(String id);
    List<T> getAllUsers();
    T updateUser(String id, T user);
    void deleteUser(String id);
}