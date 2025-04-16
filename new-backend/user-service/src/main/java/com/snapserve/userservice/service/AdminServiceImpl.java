package com.snapserve.userservice.service;

import com.snapserve.userservice.model.Admin;
import com.snapserve.userservice.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements GenericUserService<Admin> {

    private final AdminRepository adminRepository;

    @Override
    public Admin createUser(Admin user) {
        return adminRepository.save(user);
    }

    @Override
    public Admin getUserById(String id) {
        return adminRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    @Override
    public List<Admin> getAllUsers() {
        return adminRepository.findAll();
    }

    @Override
    public Admin updateUser(String id, Admin user) {
        user.setId(new ObjectId(id));
        return adminRepository.save(user);
    }

    @Override
    public void deleteUser(String id) {
        adminRepository.deleteById(new ObjectId(id));
    }
}
