package com.snapserve.notification.config;

import com.snapserve.notification.model.NotificationTemplate;
import com.snapserve.notification.repository.NotificationTemplateRepository;
import com.snapserve.notificationclient.constants.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateSeeder implements CommandLineRunner {

  private final NotificationTemplateRepository templateRepository;

  @Override
  public void run(String... args) {
    // Only seed if no templates exist
    if (templateRepository.count() == 0) {
      log.info("Seeding initial notification templates...");

      createTemplate(
          "REGISTRATION_SUCCESS",
          NotificationChannel.EMAIL,
          "Welcome to SnapServe!",
          buildRegistrationTemplate(),
          "Welcome to SnapServe, {name}! Your registration was successful.");

      createTemplate(
          "BOOKING_CONFIRMATION",
          NotificationChannel.EMAIL,
          "Booking Confirmed - {bookingId}",
          buildBookingConfirmationTemplate(),
          "Hi {customerName}, your booking {bookingId} is confirmed for {appointmentTime}.");

      createTemplate(
          "BOOKING_CANCELLATION",
          NotificationChannel.EMAIL,
          "Booking Cancelled - {bookingId}",
          buildBookingCancellationTemplate(),
          "Hi {customerName}, your booking {bookingId} has been cancelled.");

      createTemplate(
          "BOOKING_COMPLETION",
          NotificationChannel.EMAIL,
          "Booking Completed - {bookingId}",
          buildBookingCompletionTemplate(),
          "Hi {customerName}, your booking {bookingId} has been completed. Thank you!");

      createTemplate(
          "PASSWORD_RESET",
          NotificationChannel.EMAIL,
          "Password Reset Request",
          buildPasswordResetTemplate(),
          "Hi {name}, use code {resetCode} to reset your password. This code expires in 1 hour.");

      log.info("Templates seeded successfully");
    }
  }

  private void createTemplate(
      String name, NotificationChannel channel, String subject, String bodyHtml, String bodyText) {

    NotificationTemplate template = new NotificationTemplate();
    template.setId(new ObjectId());
    template.setName(name);
    template.setChannel(channel);
    template.setSubject(subject);
    template.setBodyHtml(bodyHtml);
    template.setBodyText(bodyText);
    template.setVersion(1);
    template.setActive(true);

    templateRepository.save(template);
    log.info("Created template: {}", name);
  }

  private String buildRegistrationTemplate() {
    return "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "    <meta charset=\"UTF-8\">\n"
        + "    <title>Welcome to SnapServe</title>\n"
        + "</head>\n"
        + "<body style=\"font-family: Arial, sans-serif; line-height: 1.6;\">\n"
        + "    <h2>Hello, <span th:text=\"${name}\">[Name]</span>!</h2>\n"
        + "    <p>Welcome to SnapServe! We're excited to have you on board.</p>\n"
        + "    <p>Your registration was successful. You can now book services with our specialists.</p>\n"
        + "    <hr>\n"
        + "    <footer>\n"
        + "        <p>Best Regards,<br>SnapServe Team</p>\n"
        + "    </footer>\n"
        + "</body>\n"
        + "</html>";
  }

  private String buildBookingConfirmationTemplate() {
    return "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "    <meta charset=\"UTF-8\">\n"
        + "    <title>Booking Confirmed</title>\n"
        + "</head>\n"
        + "<body style=\"font-family: Arial, sans-serif; line-height: 1.6;\">\n"
        + "    <h2>Hello, <span th:text=\"${customerName}\">[Name]</span>!</h2>\n"
        + "    <p>Your booking has been confirmed!</p>\n"
        + "    <p><strong>Booking ID:</strong> <span th:text=\"${bookingId}\">[ID]</span></p>\n"
        + "    <p><strong>Appointment Time:</strong> <span th:text=\"${appointmentTime}\">[Time]</span></p>\n"
        + "    <p>We're looking forward to serving you.</p>\n"
        + "    <hr>\n"
        + "    <footer>\n"
        + "        <p>Best Regards,<br>SnapServe Team</p>\n"
        + "    </footer>\n"
        + "</body>\n"
        + "</html>";
  }

  private String buildBookingCancellationTemplate() {
    return "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "    <meta charset=\"UTF-8\">\n"
        + "    <title>Booking Cancelled</title>\n"
        + "</head>\n"
        + "<body style=\"font-family: Arial, sans-serif; line-height: 1.6;\">\n"
        + "    <h2>Hello, <span th:text=\"${customerName}\">[Name]</span>!</h2>\n"
        + "    <p>Your booking has been cancelled.</p>\n"
        + "    <p><strong>Booking ID:</strong> <span th:text=\"${bookingId}\">[ID]</span></p>\n"
        + "    <p>If this was a mistake or you have questions, please contact us.</p>\n"
        + "    <hr>\n"
        + "    <footer>\n"
        + "        <p>Best Regards,<br>SnapServe Team</p>\n"
        + "    </footer>\n"
        + "</body>\n"
        + "</html>";
  }

  private String buildBookingCompletionTemplate() {
    return "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "    <meta charset=\"UTF-8\">\n"
        + "    <title>Booking Completed</title>\n"
        + "</head>\n"
        + "<body style=\"font-family: Arial, sans-serif; line-height: 1.6;\">\n"
        + "    <h2>Hello, <span th:text=\"${customerName}\">[Name]</span>!</h2>\n"
        + "    <p>Your booking has been completed successfully!</p>\n"
        + "    <p><strong>Booking ID:</strong> <span th:text=\"${bookingId}\">[ID]</span></p>\n"
        + "    <p>Thank you for choosing SnapServe. We hope you had a great experience!</p>\n"
        + "    <hr>\n"
        + "    <footer>\n"
        + "        <p>Best Regards,<br>SnapServe Team</p>\n"
        + "    </footer>\n"
        + "</body>\n"
        + "</html>";
  }

  private String buildPasswordResetTemplate() {
    return "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "    <meta charset=\"UTF-8\">\n"
        + "    <title>Password Reset</title>\n"
        + "</head>\n"
        + "<body style=\"font-family: Arial, sans-serif; line-height: 1.6;\">\n"
        + "    <h2>Hello, <span th:text=\"${name}\">[Name]</span>!</h2>\n"
        + "    <p>You requested a password reset. Use the following code:</p>\n"
        + "    <h3 style=\"background: #f0f0f0; padding: 10px; text-align: center;\">\n"
        + "        <span th:text=\"${resetCode}\">[Code]</span>\n"
        + "    </h3>\n"
        + "    <p>This code expires in 1 hour.</p>\n"
        + "    <hr>\n"
        + "    <footer>\n"
        + "        <p>Best Regards,<br>SnapServe Team</p>\n"
        + "    </footer>\n"
        + "</body>\n"
        + "</html>";
  }
}
