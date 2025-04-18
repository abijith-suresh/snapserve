package com.snapserve.userservice.service;

import com.snapserve.userservice.dto.AdminRequest;
import com.snapserve.userservice.dto.AdminResponse;
import com.snapserve.userservice.exception.ResourceNotFoundException;
import com.snapserve.userservice.mapper.AdminMapper;
import com.snapserve.userservice.model.Admin;
import com.snapserve.userservice.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements GenericUserService<Admin, AdminRequest, AdminResponse> {

    private final AdminRepository adminRepository;

    @Override
    public AdminResponse createUser(AdminRequest request) {
        Admin admin = AdminMapper.toEntity(request);
        Admin saved = adminRepository.save(admin);
        return AdminMapper.toResponse(saved);
    }

    @Override
    public AdminResponse getUserById(String id) {
        Admin admin = adminRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Admin", id));
        return AdminMapper.toResponse(admin);
    }

    @Override
    public List<AdminResponse> getAllUsers() {
        return adminRepository.findAll()
                .stream()
                .map(AdminMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminResponse updateUser(String id, AdminRequest request) {
        Admin existing = adminRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Admin", id));

        Admin updated = AdminMapper.toEntity(request);
        updated.setId(existing.getId());

        Admin saved = adminRepository.save(updated);
        return AdminMapper.toResponse(saved);
    }

    @Override
    public void deleteUser(String id) {
        Admin admin = adminRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Admin", id));
        adminRepository.delete(admin);
    }
}

