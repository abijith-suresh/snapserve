# Notification Service Agent Context

Email notification service. Currently being rebuilt. Port: 9003.

## Status

This service is under reconstruction. Basic structure exists but email functionality is being redesigned.

## Quick Commands

```bash
# Build
./gradlew :backend:notification-service:build

# Test
./gradlew :backend:notification-service:test

# Docker
docker compose up -d notification-service

# Health check
curl http://localhost:9003/actuator/health
```

## Key Files

| File | Purpose |
|------|---------|
| `NotificationServiceApplication.java` | Entry point |
| `controller/NotificationController.java` | REST endpoints |

## Planned Architecture

- Gmail SMTP for email delivery
- Template-based rendering (Thymeleast)
- Async queue processing
- Multi-provider support (SendGrid, AWS SES)

## Configuration

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${GMAIL_USERNAME}
    password: ${GMAIL_APP_PASSWORD}
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
```

## Environment Variables

```yaml
GMAIL_USERNAME: Gmail address
GMAIL_APP_PASSWORD: Gmail App Password (not regular password)
```

## Getting Gmail App Password

1. Enable 2FA on Google account
2. Visit https://myaccount.google.com/apppasswords
3. Generate for "Mail"
4. Use 16-character password

## Service-Specific Rules

1. **No blocking**: Use async processing for emails.
2. **Templates**: Use Thymeleaf for HTML emails.
3. **Retry logic**: Implement exponential backoff.
4. **Logging**: Log all email attempts (success/failure).
5. **Rate limits**: Respect Gmail limits (100/day for new accounts).

## Full Documentation

- [Notification Service Docs](../../docs/backend/notification-service.md)
- [Architecture Overview](../../AGENTS.md)

## Port

9003
