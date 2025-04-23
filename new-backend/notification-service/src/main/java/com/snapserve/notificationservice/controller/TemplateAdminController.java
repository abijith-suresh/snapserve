package com.snapserve.notificationservice.controller;

import com.snapserve.notificationservice.dto.request.TemplateRequest;
import com.snapserve.notificationservice.dto.response.TemplateResponse;
import com.snapserve.notificationservice.service.TemplateAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/templates")
@RequiredArgsConstructor
public class TemplateAdminController {

    private final TemplateAdminService templateAdminService;

    @PostMapping
    public ResponseEntity<TemplateResponse> createTemplate(@Valid @RequestBody TemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(templateAdminService.createTemplate(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemplateResponse> updateTemplate(@PathVariable String id,
                                                           @Valid @RequestBody TemplateRequest request) {
        return ResponseEntity.ok(templateAdminService.updateTemplate(id, request));
    }

    @GetMapping
    public ResponseEntity<List<TemplateResponse>> getAllTemplates() {
        return ResponseEntity.ok(templateAdminService.getAllTemplates());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TemplateResponse> getTemplateByName(@PathVariable String name) {
        return ResponseEntity.ok(templateAdminService.getTemplateByName(name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        templateAdminService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeTemplateStatus(@PathVariable String id, @RequestParam boolean active) {
        if (active) {
            templateAdminService.enableTemplate(id);
        } else {
            templateAdminService.disableTemplate(id);
        }
        return ResponseEntity.noContent().build();
    }

}

