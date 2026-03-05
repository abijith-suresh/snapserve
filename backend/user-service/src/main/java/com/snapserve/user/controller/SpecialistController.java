package com.snapserve.user.controller;

import com.snapserve.user.dto.AddSpecialistDto;
import com.snapserve.user.dto.EmailUpdateDto;
import com.snapserve.user.dto.SpecialistDto;
import com.snapserve.user.model.Specialist;
import com.snapserve.user.service.SpecialistService;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/specialists")
public class SpecialistController {

  @Autowired private SpecialistService specialistService;

  @GetMapping("/")
  public ResponseEntity<List<SpecialistDto>> getAllSpecialists() {
    return ResponseEntity.ok(specialistService.getAllSpecialists());
  }

  @GetMapping("/{id}")
  public ResponseEntity<SpecialistDto> getSpecialistById(@PathVariable String id) {
    SpecialistDto specialist = specialistService.getSpecialistById(new ObjectId(id));
    if (specialist == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(specialist);
  }

  @PostMapping("/")
  public ResponseEntity<Specialist> createSpecialist(
      @RequestBody AddSpecialistDto addSpecialistDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(specialistService.createSpecialist(addSpecialistDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Specialist> updateSpecialist(
      @PathVariable String id, @RequestBody Specialist specialistDetails) {
    Specialist updated = specialistService.updateSpecialist(new ObjectId(id), specialistDetails);
    if (updated == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSpecialist(@PathVariable String id) {
    specialistService.deleteSpecialist(new ObjectId(id));
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<SpecialistDto> getSpecialistByEmail(@PathVariable String email) {
    SpecialistDto specialist = specialistService.findByEmail(email);
    if (specialist == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(specialist);
  }

  @PutMapping("/{id}/update-email")
  public ResponseEntity<Specialist> updateSpecialistEmail(
      @PathVariable String id, @RequestBody EmailUpdateDto emailUpdateDto) {
    SpecialistDto existing = specialistService.findByEmail(emailUpdateDto.getEmail());
    if (existing != null) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    Specialist updated = specialistService.updateSpecialistEmail(new ObjectId(id), emailUpdateDto);
    if (updated == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/email/{email}")
  public ResponseEntity<Void> deleteSpecialistByEmail(@PathVariable String email) {
    specialistService.deleteSpecialistByEmail(email);
    return ResponseEntity.noContent().build();
  }
}
