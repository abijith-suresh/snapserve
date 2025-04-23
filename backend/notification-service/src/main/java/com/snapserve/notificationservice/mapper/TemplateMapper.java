package com.snapserve.notificationservice.mapper;

import com.snapserve.notificationservice.dto.request.TemplateRequest;
import com.snapserve.notificationservice.dto.response.TemplateResponse;
import com.snapserve.notificationservice.model.NotificationTemplate;

public class TemplateMapper {

    public static NotificationTemplate toEntity(TemplateRequest request) {
        return NotificationTemplate.builder()
                .name(request.getName())
                .subject(request.getSubject())
                .content(request.getContent())
                .active(true)
                .build();
    }

    public static TemplateResponse toResponse(NotificationTemplate template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .subject(template.getSubject())
                .content(template.getContent())
                .active(template.isActive())
                .build();
    }
}

