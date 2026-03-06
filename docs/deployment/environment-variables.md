# Environment Variables

Complete reference for all environment variables.

## Required Variables

These must be set in `.env` file:

### MongoDB

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `MONGODB_URI` | Yes* | - | MongoDB connection string |

*Required if not using local MongoDB container

Atlas format:
```
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/snapserve?retryWrites=true&w=majority
```

Local format:
```
MONGODB_URI=mongodb://admin:password@mongo:27017/snapserve?authSource=admin
```

### JWT Security

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `JWT_SECRET` | Yes | - | Signing key (≥64 chars) |

Generate a secure secret:
```bash
openssl rand -base64 64
```

Example:
```
JWT_SECRET=your-very-long-secret-key-at-least-64-characters-long-here...
```

### Gmail SMTP (Notification Service)

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `GMAIL_USERNAME` | Yes* | - | Gmail address |
| `GMAIL_APP_PASSWORD` | Yes* | - | Gmail App Password |

*Required only if using notification-service

How to get App Password:
1. Enable 2FA on Google account
2. Visit https://myaccount.google.com/apppasswords
3. Generate app password for "Mail"
4. Copy the 16-character password

Example:
```
GMAIL_USERNAME=notifications@example.com
GMAIL_APP_PASSWORD=abcd efgh ijkl mnop
```

### CORS

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `ALLOWED_ORIGINS` | No | `http://localhost:3000` | Comma-separated allowed origins |

For multiple origins:
```
ALLOWED_ORIGINS=http://localhost:3000,https://app.example.com
```

## Optional Variables

### Docker Compose Settings

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `RESTART_POLICY` | No | `no` | Container restart policy |

Options: `no`, `always`, `unless-stopped`

### MongoDB Container (Local Only)

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `MONGO_ROOT_USER` | No | `admin` | MongoDB root username |
| `MONGO_ROOT_PASSWORD` | No | `password` | MongoDB root password |

Only used when running MongoDB via Docker Compose.

### Service URLs (Development)

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `AUTH_SERVICE_URL` | No | `http://localhost:9000` | Auth service URL |
| `USER_SERVICE_URL` | No | `http://localhost:9001` | User service URL |
| `BOOKING_SERVICE_URL` | No | `http://localhost:9002` | Booking service URL |
| `NOTIFICATION_SERVICE_URL` | No | `http://localhost:9003` | Notification service URL |

In Docker Compose, these are overridden to use service names.

## Complete .env Example

```bash
# ============================================
# SnapServe Environment Configuration
# ============================================

# --- Database (Required) ---
# MongoDB Atlas or local
MONGODB_URI=mongodb+srv://user:password@cluster.mongodb.net/snapserve?retryWrites=true&w=majority

# --- Security (Required) ---
# Generate: openssl rand -base64 64
JWT_SECRET=your-64-character-minimum-secret-key-here-change-in-production

# --- Email (Required for notifications) ---
GMAIL_USERNAME=your-email@gmail.com
GMAIL_APP_PASSWORD=xxxx xxxx xxxx xxxx

# --- CORS (Optional) ---
ALLOWED_ORIGINS=http://localhost:3000

# --- Docker Compose (Optional) ---
RESTART_POLICY=unless-stopped

# --- Local MongoDB (Optional) ---
# Only needed if using local MongoDB container
MONGO_ROOT_USER=admin
MONGO_ROOT_PASSWORD=changeme

# --- Development URLs (Optional) ---
# Only needed when running services outside Docker
AUTH_SERVICE_URL=http://localhost:9000
USER_SERVICE_URL=http://localhost:9001
BOOKING_SERVICE_URL=http://localhost:9002
NOTIFICATION_SERVICE_URL=http://localhost:9003
```

## Security Checklist

- [ ] JWT_SECRET is ≥64 characters
- [ ] JWT_SECRET is different in each environment
- [ ] GMAIL_APP_PASSWORD is not your regular Gmail password
- [ ] MongoDB password is strong (if using local)
- [ ] .env file is in .gitignore
- [ ] Production credentials are not committed

## Environment-Specific Values

### Development

```
ALLOWED_ORIGINS=http://localhost:3000
RESTART_POLICY=no
```

### Staging

```
ALLOWED_ORIGINS=https://staging.example.com
RESTART_POLICY=unless-stopped
```

### Production

```
ALLOWED_ORIGINS=https://app.example.com
RESTART_POLICY=always
# Use strong, unique JWT_SECRET
# Use production MongoDB cluster
# Use production email service
```

## Validation

The application validates required env vars on startup. Missing required variables will cause the service to fail fast with a clear error message.

## Secrets Management

For production, consider:
- AWS Secrets Manager
- HashiCorp Vault
- Kubernetes Secrets
- Docker Swarm secrets

Never commit `.env` files containing real credentials.
