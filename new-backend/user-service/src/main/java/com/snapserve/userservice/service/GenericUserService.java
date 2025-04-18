package com.snapserve.userservice.service;

import java.util.List;

public interface GenericUserService<ENTITY, REQUEST_DTO, RESPONSE_DTO> {
    RESPONSE_DTO createUser(REQUEST_DTO requestDto);
    RESPONSE_DTO getUserById(String id);
    List<RESPONSE_DTO> getAllUsers();
    RESPONSE_DTO updateUser(String id, REQUEST_DTO requestDto);
    void deleteUser(String id);
}