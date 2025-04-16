package com.snapserve.userservice.controller;

import com.snapserve.userservice.model.Specialist;
import com.snapserve.userservice.service.GenericUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialists")
@RequiredArgsConstructor
public class SpecialistController {

    private GenericUserService<Specialist> specialistService;

    @PostMapping
    public Specialist create(@RequestBody Specialist specialist) {
        return specialistService.createUser(specialist);
    }

    @GetMapping("/{id}")
    public Specialist get(@PathVariable String id) {
        return specialistService.getUserById(id);
    }

    @GetMapping
    public List<Specialist> getAll() {
        return specialistService.getAllUsers();
    }

    @PutMapping("/{id}")
    public Specialist update(@PathVariable String id, @RequestBody Specialist specialist) {
        return specialistService.updateUser(id, specialist);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        specialistService.deleteUser(id);
    }
}