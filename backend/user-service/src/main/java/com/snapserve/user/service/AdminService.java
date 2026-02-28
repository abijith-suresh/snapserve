package com.snapserve.user.service;

import com.snapserve.user.dto.AdminDto;
import com.snapserve.user.model.Admin;
import com.snapserve.user.repo.AdminRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

  @Autowired private AdminRepository adminRepository;

  private AdminDto modelToDto(Admin admin) {
    return new AdminDto(admin.getFirstName(), admin.getLastName(), admin.getEmail());
  }

  private void dtoToModel(Admin admin, AdminDto dto) {
    admin.setFirstName(dto.getFirstName());
    admin.setLastName(dto.getLastName());
    admin.setEmail(dto.getEmail());
  }

  public List<AdminDto> findAllAdmins() {
    return adminRepository.findAll().stream().map(this::modelToDto).collect(Collectors.toList());
  }

  public Admin findAdminById(ObjectId id) {
    return adminRepository.findById(id).orElse(null);
  }

  public Admin createAdmin(AdminDto dto) {
    Admin admin = new Admin();
    dtoToModel(admin, dto);
    return adminRepository.save(admin);
  }

  public Admin updateAdmin(ObjectId id, Admin details) {
    details.setId(id);
    return adminRepository.save(details);
  }

  public void deleteAdminById(ObjectId id) {
    adminRepository.deleteById(id);
  }

  public Admin findByEmail(String email) {
    return adminRepository.findByEmail(email).orElse(null);
  }
}
