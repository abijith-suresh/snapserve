package com.snapserve.notification.strategy;

import com.snapserve.notification.model.NotificationTemplate;
import com.snapserve.notificationclient.constants.NotificationChannel;
import com.snapserve.notificationclient.request.SendNotificationRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationStrategy implements NotificationChannelStrategy {

  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.username}")
  private String fromAddress;

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
      String recipient,
      String subject,
      String bodyHtml,
      NotificationTemplate template,
      Map<String, Object> parameters)
      throws MessagingException {

    Context context = new Context();
    if (parameters != null) {
      parameters.forEach(context::setVariable);
    }

    String processedHtml = templateEngine.process(template.getBodyHtml(), context);

    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    helper.setTo(recipient);
    helper.setSubject(subject);
    helper.setText(processedHtml, true);
    helper.setFrom(fromAddress);

    javaMailSender.send(mimeMessage);
    log.info("Email sent successfully to: {}", recipient);
  }
}
