package com.snapserve.notification.service;

import com.snapserve.notification.exception.TemplateNotFoundException;
import com.snapserve.notification.model.NotificationTemplate;
import com.snapserve.notification.repository.NotificationTemplateRepository;
import com.snapserve.notificationclient.constants.NotificationChannel;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

  private final NotificationTemplateRepository templateRepository;

  public NotificationTemplate getTemplate(String name, NotificationChannel channel) {
    return templateRepository
        .findByNameAndChannelAndActiveTrue(name, channel)
        .orElseThrow(() -> new TemplateNotFoundException(name + " (" + channel + ")"));
  }

  public List<NotificationTemplate> getAllTemplates() {
    return templateRepository.findAll();
  }

  public NotificationTemplate getTemplateByName(String name) {
    return templateRepository
        .findByName(name)
        .orElseThrow(() -> new TemplateNotFoundException(name));
  }

  public NotificationTemplate createTemplate(NotificationTemplate template) {
    template.setId(new ObjectId());
    template.setVersion(1);
    template.setActive(true);
    return templateRepository.save(template);
  }

  public NotificationTemplate updateTemplate(String name, NotificationTemplate updatedTemplate) {
    NotificationTemplate existing = getTemplateByName(name);
    existing.setSubject(updatedTemplate.getSubject());
    existing.setBodyHtml(updatedTemplate.getBodyHtml());
    existing.setBodyText(updatedTemplate.getBodyText());
    existing.setVersion(existing.getVersion() + 1);
    return templateRepository.save(existing);
  }

  public void deleteTemplate(String name) {
    NotificationTemplate template = getTemplateByName(name);
    template.setActive(false);
    templateRepository.save(template);
  }

  public String processTextTemplate(String template, Map<String, Object> parameters) {
    if (template == null || parameters == null || parameters.isEmpty()) {
      return template;
    }

    String processedTemplate = template;
    for (Map.Entry<String, Object> entry : parameters.entrySet()) {
      processedTemplate =
          processedTemplate.replace(
              "{" + entry.getKey() + "}",
              entry.getValue() == null ? "" : entry.getValue().toString());
    }

    return processedTemplate;
  }
}
