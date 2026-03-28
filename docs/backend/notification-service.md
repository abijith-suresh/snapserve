# Notification Service

Sends email notifications to users.

## Purpose

Handles outbound email communication:
- Welcome emails for new registrations
- Booking confirmations and updates
- Status change notifications
- Review notifications

## Current Status

**Rebuilding in progress.**

The notification service is being refactored to improve reliability and add new features. The previous implementation has been removed and a new version is being developed.

## Planned Features

### Email Templates

HTML email templates using Thymeleaf:
- Welcome email (new user registration)
- Booking confirmation (to customer and specialist)
- Booking status updates
- Review notifications
- Password reset emails

### Queue Processing

Asynchronous email sending:
- Emails queued for processing
- Retry logic for failed sends
- Rate limiting to prevent spam

### Supported Providers

Initial implementation:
- Gmail SMTP
- Configurable via environment variables

Future additions:
- SendGrid
- AWS SES
- Mailgun

## API Endpoints

### POST /api/v1/notifications/email

Send email notification.

**Access**: Internal (other services only)

**Request**:
- to (string) — Recipient email
- subject (string) — Email subject
- template (string) — Template name
- data (object) — Template variables

**Response**: 202 Accepted (queued for sending)

### POST /api/v1/notifications/sms

Send SMS notification.

**Status**: Planned for future release

## Integration

Other services call notification service via HTTP:

**Auth Service calls:**
- Welcome email on registration

**Booking Service calls:**
- Booking confirmation emails
- Status update notifications

**User Service calls:**
- Profile update confirmations

## Architecture

### Components (Planned)

**NotificationController**
- REST endpoints for sending notifications
- Request validation
- Queue integration

**NotificationService**
- Template rendering (Thymeleaf)
- Email provider abstraction
- Queue management

**EmailSender**
- SMTP integration
- Retry logic
- Error handling

**Templates**
- Thymeleaf HTML templates
- Shared layout components
- Localization support

## Dependencies (Planned)

- Spring Boot Web
- Spring Mail (SMTP)
- Thymeleaf (templating)
- Queue system (TBD: RabbitMQ or Kafka)

## Configuration

Environment variables:
- `GMAIL_USERNAME` — SMTP username
- `GMAIL_APP_PASSWORD` — SMTP password
- `NOTIFICATION_QUEUE_TYPE` — Queue provider (future)

## Development Status

### Completed
- Service structure
- Basic controller skeleton

### In Progress
- Email template design
- SMTP integration
- Queue implementation

### Planned
- SMS notifications
- Push notifications
- Notification preferences
- Multi-language support

## Links

- Service source: `backend/notification-service/`
- Email templates: `backend/notification-service/src/main/resources/templates/`
