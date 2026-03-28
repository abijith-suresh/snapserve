# SnapServe Documentation

Welcome to the SnapServe documentation. This is the comprehensive source of truth for the entire project.

## Quick Navigation

### Architecture
- [System Overview](./architecture/overview.md) — High-level architecture and design principles
- [Service Interactions](./architecture/service-interactions.md) — How microservices communicate

### Standards
- [Code Style](./standards/code-style.md) — Java and TypeScript formatting standards
- [API Design](./standards/api-design.md) — REST API conventions and patterns
- [Commit Conventions](./standards/commit-conventions.md) — Conventional commits guide
- [Security](./standards/security.md) — Authentication, authorization, and security practices

### Backend
- [API Gateway](./backend/api-gateway.md) — Routing, authentication, and CORS
- [Auth Service](./backend/auth-service.md) — JWT authentication flows
- [User Service](./backend/user-service.md) — Profile management
- [Booking Service](./backend/booking-service.md) — Bookings and reviews
- [Notification Service](./backend/notification-service.md) — Email notifications
- [Shared Libraries](./backend/shared-libraries.md) — Common module usage

### Frontend
- [Architecture](./frontend/architecture.md) — Application structure and patterns
- [State Management](./frontend/state-management.md) — Zustand and React Query patterns
- [Routing](./frontend/routing.md) — React Router configuration

### Deployment
- [Docker Compose](./deployment/docker-compose.md) — Local development orchestration
- [Environment Variables](./deployment/environment-variables.md) — All configuration options

## Project Overview

SnapServe is a service booking platform built as a microservices architecture:

- **Backend**: 5 Spring Boot microservices (Java 21, Spring Boot 4.0.3)
- **Frontend**: React 19 SPA (TypeScript, Vite, Tailwind CSS v4)
- **Database**: MongoDB
- **Communication**: REST APIs through API Gateway
- **Infrastructure**: Docker Compose, GitHub Actions CI/CD

## Current Status

- **Backend**: Refactoring in progress to standardize patterns
- **Frontend**: Landing page complete, dashboards being built
- **API Integration**: Will be implemented after backend stabilization
- **Tests**: Will be added after architecture is finalized

## Getting Started

1. Copy `.env.example` to `.env` and fill in values
2. Run `docker compose up --build`
3. Frontend: http://localhost:3000
4. API Gateway: http://localhost:9090

See [Architecture Overview](./architecture/overview.md) for detailed setup instructions.
