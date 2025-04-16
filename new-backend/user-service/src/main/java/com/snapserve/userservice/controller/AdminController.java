package com.snapserve.userservice.controller;

import com.snapserve.userservice.model.Admin;
import com.snapserve.userservice.service.GenericUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private GenericUserService<Admin> adminService;

    @PostMapping
    public Admin create(@RequestBody Admin admin) {
        return adminService.createUser(admin);
    }

    @GetMapping("/{id}")
    public Admin get(@PathVariable String id) {
        return adminService.getUserById(id);
    }

    @GetMapping
    public List<Admin> getAll() {
        return adminService.getAllUsers();
    }

    @PutMapping("/{id}")
    public Admin update(@PathVariable String id, @RequestBody Admin admin) {
        return adminService.updateUser(id, admin);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        adminService.deleteUser(id);
    }
}
