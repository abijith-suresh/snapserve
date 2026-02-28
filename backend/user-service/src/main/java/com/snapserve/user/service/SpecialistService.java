package com.snapserve.user.service;

import com.snapserve.user.dto.AddSpecialistDto;
import com.snapserve.user.dto.EmailUpdateDto;
import com.snapserve.user.dto.SpecialistDto;
import com.snapserve.user.model.Specialist;
import com.snapserve.user.repo.SpecialistRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class SpecialistService {

  @Autowired private SpecialistRepository specialistRepository;

  @Value("${notification.service.url}")
  private String notificationServiceUrl;

  private SpecialistDto modelToDto(Specialist specialist) {
    return new SpecialistDto(
        specialist.getId().toString(),
        specialist.getName(),
        specialist.getEmail(),
        specialist.getPhoneNumber(),
        specialist.getTitle(),
        specialist.getBio(),
        specialist.getPrice(),
        specialist.getRating(),
        specialist.getProfileImage(),
        specialist.getServices(),
        specialist.getPhotos(),
        specialist.getExperience(),
        specialist.getAddress(),
        specialist.getStatus());
  }

  private void dtoToModel(Specialist specialist, AddSpecialistDto dto) {
    specialist.setName(dto.getName());
    specialist.setEmail(dto.getEmail());
    specialist.setPhoneNumber(dto.getPhoneNumber());
    specialist.setTitle(dto.getTitle());
    specialist.setBio(dto.getBio());
    specialist.setPrice(dto.getPrice());
    specialist.setRating(dto.getRating());
    specialist.setProfileImage(dto.getProfileImage());
    specialist.setServices(dto.getServices());
    specialist.setPhotos(dto.getPhotos());
    specialist.setExperience(dto.getExperience());
    specialist.setAddress(dto.getAddress());
    specialist.setStatus(dto.getStatus());
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
    Specialist saved = specialistRepository.save(specialist);
    sendRegistrationEmail(saved.getEmail(), saved.getName());
    return saved;
  }

  public Specialist updateSpecialist(ObjectId id, Specialist details) {
    details.setId(id);
    return specialistRepository.save(details);
  }

  public void deleteSpecialist(ObjectId id) {
    specialistRepository.deleteById(id);
  }

  public SpecialistDto findByEmail(String email) {
    return specialistRepository.findByEmail(email).map(this::modelToDto).orElse(null);
  }

  public Specialist updateSpecialistEmail(ObjectId id, EmailUpdateDto dto) {
    return specialistRepository
        .findById(id)
        .map(
            existing -> {
              existing.setEmail(dto.getEmail());
              return specialistRepository.save(existing);
            })
        .orElse(null);
  }

  public void updateSpecialistStatus(ObjectId id, String status) {
    specialistRepository
        .findById(id)
        .ifPresent(
            specialist -> {
              specialist.setStatus(status);
              specialistRepository.save(specialist);
            });
  }

  public List<SpecialistDto> getSpecialistsByStatus(String status) {
    return specialistRepository.findByStatus(status).stream()
        .map(this::modelToDto)
        .collect(Collectors.toList());
  }

  public void deleteSpecialistByEmail(String email) {
    specialistRepository.findByEmail(email).ifPresent(specialistRepository::delete);
  }

  private void sendRegistrationEmail(String to, String name) {
    try {
      RestClient.create()
          .post()
          .uri(
              notificationServiceUrl
                  + "/api/notifications/send-registration-success?to={to}&name={name}",
              to,
              name)
          .retrieve()
          .toBodilessEntity();
    } catch (RestClientException e) {
      System.out.println("Failed to send registration email: " + e.getMessage());
    }
  }
}
