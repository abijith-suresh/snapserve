package com.snapserve.user.service;

import com.snapserve.user.dto.AddSpecialistDto;
import com.snapserve.user.model.Specialist;
import com.snapserve.user.repo.SpecialistRepository;
import com.snapserve.userclient.dto.SpecialistDto;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecialistService {

  @Autowired private SpecialistRepository specialistRepository;

  private SpecialistDto modelToDto(Specialist specialist) {
    return new SpecialistDto(
        specialist.getId().toString(),
        specialist.getName(),
        specialist.getEmail(),
        specialist.getPhone(),
        specialist.getTitle(),
        specialist.getServices());
  }

  private void dtoToModel(Specialist specialist, AddSpecialistDto dto) {
    specialist.setName(dto.getName());
    specialist.setEmail(dto.getEmail());
    specialist.setPhone(dto.getPhone());
    specialist.setTitle(dto.getTitle());
    specialist.setServices(dto.getServices());
  }

  public List<SpecialistDto> getAllSpecialists() {
    return specialistRepository.findAll().stream()
        .map(this::modelToDto)
        .collect(Collectors.toList());
  }

  public SpecialistDto getSpecialistById(ObjectId id) {
    return specialistRepository.findById(id).map(this::modelToDto).orElse(null);
  }

  public Specialist createSpecialist(AddSpecialistDto dto) {
    Specialist specialist = new Specialist();
    dtoToModel(specialist, dto);
    return specialistRepository.save(specialist);
  }

  public Specialist updateSpecialist(ObjectId id, Specialist details) {
    details.setId(id);
    return specialistRepository.save(details);
  }

  public void deleteSpecialist(ObjectId id) {
    specialistRepository.deleteById(id);
  }
}
