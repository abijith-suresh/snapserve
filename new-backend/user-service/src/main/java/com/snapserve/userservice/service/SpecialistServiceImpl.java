package com.snapserve.userservice.service;

import com.snapserve.userservice.dto.PagedResponse;
import com.snapserve.userservice.dto.SpecialistRequest;
import com.snapserve.userservice.dto.SpecialistResponse;
import com.snapserve.userservice.exception.ResourceNotFoundException;
import com.snapserve.userservice.mapper.SpecialistMapper;
import com.snapserve.userservice.model.Specialist;
import com.snapserve.userservice.repository.SpecialistRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialistServiceImpl implements GenericUserService<Specialist, SpecialistRequest, SpecialistResponse> {

    private final SpecialistRepository specialistRepository;

    @Override
    public SpecialistResponse createUser(SpecialistRequest request) {
        Specialist specialist = SpecialistMapper.toEntity(request);
        Specialist saved = specialistRepository.save(specialist);
        return SpecialistMapper.toResponse(saved);
    }

    @Override
    public SpecialistResponse getUserById(String id) {
        Specialist specialist = specialistRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Specialist", id));
        return SpecialistMapper.toResponse(specialist);
    }

    @Override
    public PagedResponse<SpecialistResponse> getAllUsers(Pageable pageable) {
        Page<Specialist> page = specialistRepository.findAll(pageable);

        List<SpecialistResponse> specialistResponses = page.getContent()
                .stream()
                .map(SpecialistMapper::toResponse)
                .toList();

        return PagedResponse.<SpecialistResponse>builder()
                .content(specialistResponses)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public SpecialistResponse updateUser(String id, SpecialistRequest request) {
        Specialist existing = specialistRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Specialist", id));

        Specialist updated = SpecialistMapper.toEntity(request);
        updated.setId(existing.getId());

        Specialist saved = specialistRepository.save(updated);
        return SpecialistMapper.toResponse(saved);
    }

    @Override
    public void deleteUser(String id) {
        Specialist specialist = specialistRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Specialist", id));
        specialistRepository.delete(specialist);
    }
}
