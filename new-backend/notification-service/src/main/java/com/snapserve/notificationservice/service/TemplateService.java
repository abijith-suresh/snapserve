package com.snapserve.notificationservice.service;

import com.snapserve.notificationservice.exception.TemplateLoadException;
import com.snapserve.notificationservice.exception.TemplateNotFoundException;
import com.snapserve.notificationservice.model.NotificationTemplate;
import com.snapserve.notificationservice.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final NotificationTemplateRepository templateRepository;
    private final TemplateEngine templateEngine;

    public String loadTemplate(String templateName, Map<String, Object> variables) {
        Optional<NotificationTemplate> dbTemplate = templateRepository.findByName(templateName);

        String templateContent;
        if (dbTemplate.isPresent()) {
            templateContent = dbTemplate.get().getContent();
        } else {
            templateContent = loadDefaultTemplate(templateName);
        }

        return renderTemplate(templateContent, variables);
    }

    private String loadDefaultTemplate(String templateName) {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("templates/" + templateName + ".html")) {

            if (inputStream == null) {
                throw new TemplateNotFoundException("Default template not found: " + templateName);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new TemplateLoadException("Failed to load default template: " + templateName, e);
        }
    }

    private String renderTemplate(String templateContent, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateContent, context);
    }
}

