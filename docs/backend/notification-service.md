# Notification Service

Email notification service for the SnapServe platform. Port: 9003.

## Status: Rebuilding

This service is currently under reconstruction. The basic structure exists but email functionality is being redesigned.

## Overview

The Notification Service handles all email communications:
- Welcome emails for new registrations
- Booking confirmations
- Appointment reminders
- Password reset notifications

## Architecture

Spring Boot application using Gmail SMTP for email delivery. Simple REST API for triggering notifications.

## Configuration

```yaml
# application.yml
server:
  port: 9003

spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${GMAIL_USERNAME}
    password: ${GMAIL_APP_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| `GMAIL_USERNAME` | Yes | Gmail address for sending |
| `GMAIL_APP_PASSWORD` | Yes | Gmail App Password (not regular password) |

## API Endpoints

Currently minimal - full API being rebuilt.

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Health check |

## Planned Features

- Template-based email rendering (Thymeleaf)
- Queue-based async processing
- Email history tracking
- Multi-provider support (SendGrid, AWS SES)
- Webhook handling for delivery status

## Key Classes

| Class | Purpose |
|-------|---------|
| `NotificationController` | REST endpoints |
| `EmailService` | Email sending logic |
| `NotificationTemplate` | Email templates |

## Integration

Called by other services via REST:
- **Auth Service**: Welcome email on registration
- **User Service**: Profile update confirmations
- **Booking Service**: Booking confirmations and reminders

## Building

```bash
# Build this service only
./gradlew :backend:notification-service:build

# Run tests
./gradlew :backend:notification-service:test
```

## Local Development

1. Create a Gmail account for testing
2. Enable 2-factor authentication
3. Generate an App Password at https://myaccount.google.com/apppasswords
4. Add to `.env`:
   ```
   GMAIL_USERNAME=your-test-email@gmail.com
   GMAIL_APP_PASSWORD=xxxx-xxxx-xxxx-xxxx
   ```

## Testing Emails

For development, you can use:
- MailHog (local SMTP capture)
- Gmail with app passwords
- AWS SES in sandbox mode

## Future Architecture

```
┌─────────────────┐     ┌──────────────┐     ┌─────────────┐
│  Other Services │────▶│  REST API    │────▶│  Queue      │
└─────────────────┘     └──────────────┘     └─────────────┘
                                                    │
                         ┌─────────────────────────┘
                         ▼
                    ┌──────────────┐
                    │  Worker      │
                    └──────────────┘
                         │
                         ▼
                    ┌──────────────┐
                    │  SMTP/SendGrid│
                    └──────────────┘
```
