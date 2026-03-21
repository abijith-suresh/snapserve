package com.snapserve.notification.strategy;

import com.snapserve.notificationclient.constants.NotificationChannel;
import com.snapserve.notificationclient.request.SendNotificationRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@Component
public class EmailNotificationStrategy implements NotificationChannelStrategy {

  private static final Logger log = LoggerFactory.getLogger(EmailNotificationStrategy.class);

  private final JavaMailSender javaMailSender;
  private final SpringTemplateEngine templateEngine;

  public EmailNotificationStrategy(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
    StringTemplateResolver templateResolver = new StringTemplateResolver();
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateResolver.setCacheable(false);

    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);
    this.templateEngine = templateEngine;
  }

  @Value("${spring.mail.username}")
  private String fromAddress;

  @Value("${spring.mail.password:}")
  private String mailPassword;

  @Override
  public boolean supports(NotificationChannel channel) {
    return channel == NotificationChannel.EMAIL;
  }

  @Override
  public void send(SendNotificationRequest request) throws Exception {
    // This method receives the template already populated by TemplateService
    // The bodyHtml in the request is the processed template
    throw new UnsupportedOperationException(
        "EmailNotificationStrategy requires template processing first");
  }

  public void sendEmail(
      String recipient, String subject, String bodyHtml, Map<String, Object> parameters)
      throws MessagingException {

    if (fromAddress == null
        || fromAddress.isBlank()
        || mailPassword == null
        || mailPassword.isBlank()) {
      throw new IllegalStateException(
          "Email delivery is not configured. Set GMAIL_USERNAME and GMAIL_APP_PASSWORD.");
    }

    String processedHtml = renderHtml(bodyHtml, parameters);

    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    helper.setTo(recipient);
    helper.setSubject(subject);
    helper.setText(processedHtml, true);
    helper.setFrom(fromAddress);

    javaMailSender.send(mimeMessage);
    log.info("Email sent successfully to: {}", recipient);
  }

  private String renderHtml(String template, Map<String, Object> parameters) {
    if (template == null) {
      return null;
    }

    Context context = new Context();
    if (parameters != null) {
      parameters.forEach(context::setVariable);
    }

    return templateEngine.process(template, context);
  }
}
