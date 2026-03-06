# Docker Compose Deployment

Local development and deployment using Docker Compose.

## Overview

The `docker-compose.yml` file defines all services for local development. Services communicate via Docker network.

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Docker Network                      │
│  snapserve-network (bridge)                         │
│                                                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │  MongoDB │  │  Auth    │  │  User    │          │
│  │  :27017  │  │  :9000   │  │  :9001   │          │
│  └──────────┘  └──────────┘  └──────────┘          │
│                                                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ Booking  │  │  Notify  │  │  Gateway │◀─:9090   │
│  │  :9002   │  │  :9003   │  │  :9090   │   External│
│  └──────────┘  └──────────┘  └──────────┘          │
└─────────────────────────────────────────────────────┘
```

## Services

### MongoDB (Optional)

```yaml
mongo:
  image: mongo:7-jammy
  container_name: snapserve-mongo
  environment:
    MONGO_INITDB_ROOT_USERNAME: ${MONGO_ROOT_USER:-admin}
    MONGO_INITDB_ROOT_PASSWORD: ${MONGO_ROOT_PASSWORD:-password}
  ports:
    - '27017:27017'
  volumes:
    - mongo_data:/data/db
  healthcheck:
    test: ['CMD', 'mongosh', '--eval', "db.adminCommand('ping')"]
```

Disable if using MongoDB Atlas. Update `MONGODB_URI` in `.env`.

### Auth Service

```yaml
auth-service:
  build:
    context: .
    dockerfile: backend/auth-service/Dockerfile
  environment:
    MONGODB_URI: ${MONGODB_URI}
    JWT_SECRET: ${JWT_SECRET}
  ports:
    - '9000:9000'
  depends_on:
    mongo:
      condition: service_healthy
```

### User Service

```yaml
user-service:
  build:
    context: .
    dockerfile: backend/user-service/Dockerfile
  environment:
    MONGODB_URI: ${MONGODB_URI}
    NOTIFICATION_SERVICE_URL: http://notification-service:9003
  ports:
    - '9001:9001'
  depends_on:
    mongo:
      condition: service_healthy
    notification-service:
      condition: service_started
```

### Booking Service

```yaml
booking-service:
  build:
    context: .
    dockerfile: backend/booking-service/Dockerfile
  environment:
    MONGODB_URI: ${MONGODB_URI}
    USER_SERVICE_URL: http://user-service:9001
    NOTIFICATION_SERVICE_URL: http://notification-service:9003
  ports:
    - '9002:9002'
  depends_on:
    mongo:
      condition: service_healthy
    user-service:
      condition: service_started
    notification-service:
      condition: service_started
```

### Notification Service

```yaml
notification-service:
  build:
    context: .
    dockerfile: backend/notification-service/Dockerfile
  environment:
    GMAIL_USERNAME: ${GMAIL_USERNAME}
    GMAIL_APP_PASSWORD: ${GMAIL_APP_PASSWORD}
  ports:
    - '9003:9003'
```

### API Gateway

```yaml
api-gateway:
  build:
    context: .
    dockerfile: backend/api-gateway/Dockerfile
  environment:
    AUTH_SERVICE_URL: http://auth-service:9000
    USER_SERVICE_URL: http://user-service:9001
    BOOKING_SERVICE_URL: http://booking-service:9002
    NOTIFICATION_SERVICE_URL: http://notification-service:9003
    JWT_SECRET: ${JWT_SECRET}
    ALLOWED_ORIGINS: ${ALLOWED_ORIGINS:-http://localhost:3000}
  ports:
    - '9090:9090'
  depends_on:
    auth-service:
      condition: service_started
    user-service:
      condition: service_started
    booking-service:
      condition: service_started
    notification-service:
      condition: service_started
```

## Quick Start

```bash
# 1. Copy environment file
cp .env.example .env

# 2. Edit .env with your values
# - MONGODB_URI (or use local MongoDB)
# - JWT_SECRET (≥64 characters)
# - GMAIL credentials (for notifications)

# 3. Start all services
docker compose up --build

# 4. Or start in background
docker compose up -d --build

# 5. View logs
docker compose logs -f

# 6. Stop services
docker compose down

# 7. Stop and remove volumes (WARNING: deletes data)
docker compose down -v
```

## Health Checks

All services include health checks:

```yaml
healthcheck:
  test: ['CMD', 'wget', '--quiet', '--tries=1', '--spider', 'http://localhost:PORT/actuator/health']
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 40s
```

Check status:
```bash
docker compose ps
docker compose exec api-gateway wget -qO- http://localhost:9090/actuator/health
```

## Service Discovery

Services reach each other by container name:
- `http://auth-service:9000`
- `http://user-service:9001`
- `http://booking-service:9002`
- `http://notification-service:9003`

## Development vs Production

### Local Development
- MongoDB container included
- Hot reload via volume mounts (future)
- Debug ports exposed

### Production
```bash
docker compose -f docker-compose.prod.yml up -d
```

Production differences:
- No exposed database ports
- Multi-stage builds
- Non-root containers
- Resource limits

## Troubleshooting

### Service won't start
```bash
# Check logs
docker compose logs service-name

# Rebuild single service
docker compose up -d --build service-name
```

### Database connection issues
```bash
# Verify MongoDB is healthy
docker compose ps

# Check connection string
docker compose exec mongo mongosh "${MONGODB_URI}"
```

### Reset everything
```bash
docker compose down -v
docker system prune -a
docker compose up --build
```
