package com.snapserve.userservice.controller;

import com.snapserve.userservice.dto.ApiResponse;
import com.snapserve.userservice.dto.SpecialistRequest;
import com.snapserve.userservice.dto.SpecialistResponse;
import com.snapserve.userservice.model.Specialist;
import com.snapserve.userservice.service.GenericUserService;
import com.snapserve.userservice.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialists")
@RequiredArgsConstructor
public class SpecialistController {

    private final GenericUserService<Specialist, SpecialistRequest, SpecialistResponse> specialistService;

    @PostMapping
    public ResponseEntity<ApiResponse<SpecialistResponse>> createUser(@Valid @RequestBody SpecialistRequest request) {
        SpecialistResponse response = specialistService.createUser(request);
        return ResponseBuilder.created(response, "Specialist created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecialistResponse>> getUser(@PathVariable String id) {
        SpecialistResponse response = specialistService.getUserById(id);
        return ResponseBuilder.ok(response, "Specialist retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecialistResponse>>> getAllUsers() {
        List<SpecialistResponse> responses = specialistService.getAllUsers();
        return ResponseBuilder.ok(responses, "Specialists retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecialistResponse>> updateUser(@PathVariable String id, @Valid @RequestBody SpecialistRequest request) {
        SpecialistResponse response = specialistService.updateUser(id, request);
        return ResponseBuilder.ok(response, "Specialist updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        specialistService.deleteUser(id);
        return ResponseBuilder.deleted("Specialist deleted successfully");
    }
}