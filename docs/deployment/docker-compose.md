# Docker Compose

Local development with Docker Compose.

## Overview

Docker Compose orchestrates all services locally:
- MongoDB database
- 5 Spring Boot microservices
- Reverse proxy (optional)

## Services

| Service | Port | Description |
|---------|------|-------------|
| mongo | 27017 | MongoDB database |
| auth-service | 9000 | Authentication |
| user-service | 9001 | User profiles |
| booking-service | 9002 | Bookings and reviews |
| notification-service | 9003 | Email notifications |
| api-gateway | 9090 | API entry point |

## Quick Start

### Prerequisites

- Docker and Docker Compose installed
- `.env` file created from `.env.example`
- At least 4GB RAM available

### Start All Services

```bash
docker compose up --build
```

This builds images and starts all containers.

### Start Without Build

```bash
docker compose up
```

Use when images are already built.

### Start in Background

```bash
docker compose up -d
```

Run services in detached mode.

### Stop Services

```bash
docker compose down
```

Stop and remove containers.

Stop without removing:
```bash
docker compose stop
```

### View Logs

```bash
docker compose logs -f [service-name]
```

Example:
```bash
docker compose logs -f api-gateway
```

## Environment Variables

Create `.env` file from `.env.example`:

```bash
cp .env.example .env
```

Fill in required values:
- `JWT_SECRET` — 64+ character secret
- `MONGODB_URI` — Database connection
- `GMAIL_*` — Email credentials

See [environment-variables.md](./environment-variables.md) for details.

## Service Dependencies

Services start in order:
1. MongoDB
2. Notification Service
3. Auth Service
4. User Service (waits for Notification)
5. Booking Service (waits for User and Notification)
6. API Gateway (waits for all services)

## Health Checks

Each service has health check endpoint:
```
GET /actuator/health
```

Docker Compose uses these to determine when services are ready.

## Data Persistence

MongoDB data persists in Docker volume:
- Volume name: `mongo-data`
- Stored at: `/data/db` in container

Data survives container restarts.

To reset data:
```bash
docker compose down -v
docker compose up --build
```

## Accessing Services

### API Gateway (Main Entry Point)

```
http://localhost:9090
```

All API requests go through here.

### MongoDB

```
mongodb://localhost:27017
```

Connect with MongoDB Compass or CLI.

### Individual Services

Direct access (development only):
- Auth: `http://localhost:9000`
- User: `http://localhost:9001`
- Booking: `http://localhost:9002`
- Notification: `http://localhost:9003`

**Note**: In production, only API Gateway is exposed.

## Troubleshooting

### Services Won't Start

Check logs:
```bash
docker compose logs [service-name]
```

Common issues:
- Missing environment variables
- Port conflicts
- Insufficient memory

### MongoDB Connection Failed

Verify:
- MongoDB container is running
- `MONGODB_URI` is correct
- No network isolation issues

### Service Unhealthy

Check health endpoint:
```bash
curl http://localhost:9000/actuator/health
```

Service may still be starting up. Wait a moment and retry.

### Port Already in Use

Change ports in `.env`:
```
AUTH_SERVICE_PORT=9001
```

Or stop the conflicting service.

### Rebuild Everything

```bash
docker compose down -v
docker compose build --no-cache
docker compose up
```

## Development Workflow

### Backend Changes

1. Make code changes
2. Rebuild specific service:
   ```bash
   docker compose up --build auth-service
   ```

### Frontend Development

Frontend runs separately (not in Docker):
```bash
cd frontend
bun run dev
```

Connects to API at `http://localhost:9090`.

### Testing APIs

Use curl, Postman, or HTTPie:
```bash
curl http://localhost:9090/api/v1/auth/login \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password"}'
```

## Production Considerations

This Docker Compose setup is for **local development only**.

For production:
- Use orchestration platform (Kubernetes, ECS)
- External database (MongoDB Atlas)
- Load balancer
- SSL/TLS termination
- Secrets management (AWS Secrets Manager, etc.)

## Links

- Docker Compose file: `docker-compose.yml`
- Environment variables: [.env.example](../../.env.example)
- Environment docs: [environment-variables.md](./environment-variables.md)
- Docker docs: https://docs.docker.com/compose/
