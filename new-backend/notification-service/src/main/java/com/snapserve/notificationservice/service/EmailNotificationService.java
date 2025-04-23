package com.snapserve.notificationservice.service;

import com.snapserve.notificationservice.dto.request.NotificationRequest;
import com.snapserve.notificationservice.exception.TemplateNotFoundException;
import com.snapserve.notificationservice.model.NotificationLog;
import com.snapserve.notificationservice.model.NotificationStatus;
import com.snapserve.notificationservice.model.NotificationType;
import com.snapserve.notificationservice.repository.NotificationLogRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;
    private final NotificationLogRepository logRepository;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:no-reply@example.com}")
    private String from;

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.EMAIL;
    }

    @Override
    public boolean sendNotification(NotificationRequest request) {
        NotificationLog log = createLogFromRequest(request);

        try {
            String htmlContent = generateHtmlFromTemplate(request);
            sendEmail(request, htmlContent);
            log.setStatus(NotificationStatus.SENT);
        } catch (TemplateNotFoundException | MessagingException | MailException e) {
            log.setStatus(NotificationStatus.FAILED);
            log.setErrorMessage(e.getMessage());
        }

        logRepository.save(log);
        return log.getStatus() == NotificationStatus.SENT;
    }

    private NotificationLog createLogFromRequest(NotificationRequest request) {
        NotificationLog log = new NotificationLog();
        log.setType(request.getType());
        log.setTo(request.getTo());
        log.setSubject(request.getSubject());
        log.setTemplateName(request.getTemplateName());
        log.setVariables(request.getVariables());
        log.setSentAt(LocalDateTime.now());
        return log;
    }

    private String generateHtmlFromTemplate(NotificationRequest request) {
        try {
            Context context = new Context();
            context.setVariables(request.getVariables());
            String templatePath = "emails/" + request.getTemplateName();
            return templateEngine.process(templatePath, context);
        } catch (Exception e) {
            throw new TemplateNotFoundException(request.getTemplateName());
        }
    }

    private void sendEmail(NotificationRequest request, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        helper.setTo(request.getTo());
        helper.setSubject(request.getSubject());
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}