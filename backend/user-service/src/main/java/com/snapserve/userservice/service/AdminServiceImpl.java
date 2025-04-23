package com.snapserve.userservice.service;

import com.snapserve.userservice.dto.request.AdminRequest;
import com.snapserve.userservice.dto.response.AdminResponse;
import com.snapserve.userservice.dto.response.PagedResponse;
import com.snapserve.userservice.exception.ResourceNotFoundException;
import com.snapserve.userservice.mapper.AdminMapper;
import com.snapserve.userservice.model.Admin;
import com.snapserve.userservice.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public PagedResponse<AdminResponse> getAllUsers(Pageable pageable) {
        Page<Admin> page = adminRepository.findAll(pageable);

        List<AdminResponse> adminResponses = page.getContent()
                .stream()
                .map(AdminMapper::toResponse)
                .toList();

        return PagedResponse.<AdminResponse>builder()
                .content(adminResponses)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
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

