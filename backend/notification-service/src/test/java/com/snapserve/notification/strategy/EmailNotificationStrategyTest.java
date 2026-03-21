package com.snapserve.notification.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EmailNotificationStrategyTest {

  @Mock private JavaMailSender javaMailSender;

  @Test
  void sendEmailRendersThymeleafHtmlTemplateParameters() throws Exception {
    EmailNotificationStrategy strategy = new EmailNotificationStrategy(javaMailSender);
    ReflectionTestUtils.setField(strategy, "fromAddress", "noreply@snapserve.com");
    ReflectionTestUtils.setField(strategy, "mailPassword", "app-password");

    MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

    strategy.sendEmail(
        "jamie@example.com",
        "Booking Completed - booking-123",
        "<p>Hello, <span th:text=\"${customerName}\">[Name]</span></p>",
        Map.of("customerName", "Jamie"));

    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(javaMailSender).send(messageCaptor.capture());

    MimeMessage sentMessage = messageCaptor.getValue();
    assertThat(sentMessage.getSubject()).isEqualTo("Booking Completed - booking-123");
    Address[] recipients = sentMessage.getRecipients(Message.RecipientType.TO);
    org.assertj.core.api.Assertions.assertThat(recipients)
        .containsExactly(new InternetAddress("jamie@example.com"));
    org.assertj.core.api.Assertions.assertThat(sentMessage.getFrom())
        .containsExactly(new InternetAddress("noreply@snapserve.com"));
    ByteArrayOutputStream rawMessage = new ByteArrayOutputStream();
    sentMessage.writeTo(rawMessage);
    org.assertj.core.api.Assertions.assertThat(rawMessage.toString())
        .contains("<p>Hello, <span>Jamie</span></p>");
  }

  @Test
  void sendEmailRejectsMissingMailConfiguration() {
    EmailNotificationStrategy strategy = new EmailNotificationStrategy(javaMailSender);
    ReflectionTestUtils.setField(strategy, "fromAddress", "");
    ReflectionTestUtils.setField(strategy, "mailPassword", "");

    assertThatThrownBy(
            () -> strategy.sendEmail("jamie@example.com", "Subject", "<p>Body</p>", Map.of()))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Email delivery is not configured. Set GMAIL_USERNAME and GMAIL_APP_PASSWORD.");
  }
}
