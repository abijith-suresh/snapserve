package com.snapserve.notificationservice.service;

import com.snapserve.notificationservice.dto.request.TemplateRequest;
import com.snapserve.notificationservice.dto.response.TemplateResponse;
import com.snapserve.notificationservice.exception.DuplicateTemplateException;
import com.snapserve.notificationservice.exception.TemplateNotFoundException;
import com.snapserve.notificationservice.mapper.TemplateMapper;
import com.snapserve.notificationservice.model.NotificationTemplate;
import com.snapserve.notificationservice.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateAdminService {

    private final NotificationTemplateRepository templateRepository;

    public TemplateResponse createTemplate(TemplateRequest request) {
        if (templateRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateTemplateException("Template with name already exists: " + request.getName());
        }

        NotificationTemplate template = TemplateMapper.toEntity(request);
        return TemplateMapper.toResponse(templateRepository.save(template));
    }

    public TemplateResponse updateTemplate(String id, TemplateRequest request) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found: " + id));

        template.setName(request.getName());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());

        return TemplateMapper.toResponse(templateRepository.save(template));
    }

    public List<TemplateResponse> getAllTemplates() {
        return templateRepository.findAll()
                .stream()
                .map(TemplateMapper::toResponse)
                .toList();
    }

    public TemplateResponse getTemplateByName(String name) {
        NotificationTemplate template = templateRepository.findByName(name)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found: " + name));

        return TemplateMapper.toResponse(template);
    }

    public void deleteTemplate(String id) {
        if (!templateRepository.existsById(id)) {
            throw new TemplateNotFoundException("Template not found: " + id);
        }
        templateRepository.deleteById(id);
    }
}
