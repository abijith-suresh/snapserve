# Environment Variables

Configuration via environment variables.

## Overview

All configuration is externalized via environment variables. No secrets or config hardcoded in code.

Create `.env` file from `.env.example` and fill in values.

## Required Variables

### JWT Configuration

**JWT_SECRET**
- Secret key for signing JWT tokens
- Must be 64+ characters
- Generate with: `openssl rand -base64 64`
- **Required for all services**

### Database

**MONGODB_URI**
- MongoDB connection string
- Format: `mongodb://localhost:27017/snapserve`
- **Required for auth, user, booking services**

### Email

**GMAIL_USERNAME**
- Gmail address for sending emails
- Example: `yourapp@gmail.com`

**GMAIL_APP_PASSWORD**
- Gmail app-specific password
- Not your regular Gmail password
- Generate at: Google Account → Security → App passwords

### CORS

**ALLOWED_ORIGINS**
- Comma-separated list of allowed origins
- Example: `http://localhost:3000,https://app.example.com`
- **Required for API Gateway**

### Service URLs (Docker Compose)

**AUTH_SERVICE_URL**
- URL for auth-service
- Default: `http://auth-service:9000`

**USER_SERVICE_URL**
- URL for user-service
- Default: `http://user-service:9001`

**BOOKING_SERVICE_URL**
- URL for booking-service
- Default: `http://booking-service:9002`

**NOTIFICATION_SERVICE_URL**
- URL for notification-service
- Default: `http://notification-service:9003`

### MongoDB Container

**MONGO_ROOT_USER**
- MongoDB admin username
- Default: `admin`

**MONGO_ROOT_PASSWORD**
- MongoDB admin password
- Default: `password`
- Change for production

### Deployment

**RESTART_POLICY**
- Docker restart policy
- Options: `no`, `on-failure`, `always`, `unless-stopped`
- Default: `no` (for development)

## Variable Categories

| Category | Variables |
|----------|-----------|
| Security | JWT_SECRET |
| Database | MONGODB_URI, MONGO_ROOT_USER, MONGO_ROOT_PASSWORD |
| Email | GMAIL_USERNAME, GMAIL_APP_PASSWORD |
| Network | ALLOWED_ORIGINS |
| Service Discovery | *_SERVICE_URL |
| Deployment | RESTART_POLICY |

## Security Best Practices

### Local Development

- Use `.env` file (gitignored)
- Don't commit `.env` to version control
- Use strong passwords even locally

### Production

- Use secrets management (AWS Secrets Manager, HashiCorp Vault)
- Rotate secrets regularly
- Use different secrets per environment
- Never log secrets
- Restrict secret access with IAM roles

### Variable Protection

**Sensitive (never expose to client):**
- JWT_SECRET
- GMAIL_APP_PASSWORD
- MONGO_ROOT_PASSWORD
- Database credentials

**Safe for client (with VITE_ prefix):**
- VITE_API_URL

## Frontend Environment

Frontend uses Vite's environment variable system:

Variables must start with `VITE_` to be exposed to client code.

**Required:**
- `VITE_API_URL` — API gateway URL
  - Development: `http://localhost:9090`
  - Production: Your production API URL

## Docker Compose

Docker Compose reads `.env` automatically.

Variables can also be set in `docker-compose.yml` environment section.

## Configuration Priority

1. Environment variables (highest priority)
2. Application properties (application.yml)
3. Default values (lowest priority)

## Validation

Services validate required variables on startup.

Missing required variable → Service fails to start with clear error message.

## Example .env File

```bash
# Security
JWT_SECRET=your-64-character-secret-here-minimum-length-required

# Database
MONGODB_URI=mongodb://mongo:27017/snapserve

# Email
GMAIL_USERNAME=yourapp@gmail.com
GMAIL_APP_PASSWORD=your-app-password

# CORS
ALLOWED_ORIGINS=http://localhost:3000

# Service URLs (Docker internal)
AUTH_SERVICE_URL=http://auth-service:9000
USER_SERVICE_URL=http://user-service:9001
BOOKING_SERVICE_URL=http://booking-service:9002
NOTIFICATION_SERVICE_URL=http://notification-service:9003

# MongoDB Container
MONGO_ROOT_USER=admin
MONGO_ROOT_PASSWORD=secure-password

# Deployment
RESTART_POLICY=no

# Frontend
VITE_API_URL=http://localhost:9090
```

## Links

- Example file: [.env.example](../../.env.example)
- Docker Compose: [docker-compose.md](./docker-compose.md)
- Security guidelines: [../standards/security.md](../standards/security.md)
