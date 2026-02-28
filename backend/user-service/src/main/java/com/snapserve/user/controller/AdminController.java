package com.snapserve.user.controller;

import com.snapserve.user.dto.AdminDto;
import com.snapserve.user.dto.SpecialistDto;
import com.snapserve.user.model.Admin;
import com.snapserve.user.service.AdminService;
import com.snapserve.user.service.SpecialistService;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

  @Autowired private AdminService adminService;

  @Autowired private SpecialistService specialistService;

  @GetMapping("/")
  public ResponseEntity<List<AdminDto>> getAllAdmins() {
    return ResponseEntity.ok(adminService.findAllAdmins());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Admin> getAdminById(@PathVariable String id) {
    Admin admin = adminService.findAdminById(new ObjectId(id));
    if (admin == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(admin);
  }

  @PostMapping("/")
  public ResponseEntity<Admin> createAdmin(@RequestBody AdminDto adminDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdmin(adminDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Admin> updateAdmin(
      @PathVariable String id, @RequestBody Admin adminDetails) {
    Admin updated = adminService.updateAdmin(new ObjectId(id), adminDetails);
    if (updated == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAdmin(@PathVariable String id) {
    adminService.deleteAdminById(new ObjectId(id));
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<Admin> getAdminByEmail(@PathVariable String email) {
    Admin admin = adminService.findByEmail(email);
    if (admin == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(admin);
  }

  @GetMapping("/specialists")
  public ResponseEntity<List<SpecialistDto>> getAllSpecialists() {
    return ResponseEntity.ok(specialistService.getAllSpecialists());
  }

  @GetMapping("/specialist/{id}")
  public ResponseEntity<SpecialistDto> getSpecialistById(@PathVariable String id) {
    SpecialistDto specialist = specialistService.getSpecialistById(new ObjectId(id));
    if (specialist == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(specialist);
  }
}
