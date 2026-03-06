# User Service

Manages user profiles for both customers and specialists. Port: 9001.

## Overview

The User Service handles all user profile operations including:
- Customer profile CRUD operations
- Specialist profile CRUD operations  
- Service categorization for specialists
- Verification status management

## Architecture

Uses Spring Boot with MongoDB for flexible document storage. Each user is stored as a single document with role-specific fields.

## Key Entities

### UserEntity

Single collection `users` stores both customer and specialist data:

| Field | Type | Description |
|-------|------|-------------|
| `id` | ObjectId | Primary key |
| `email` | String | Unique identifier |
| `name` | String | Display name |
| `role` | Role | CUSTOMER or SPECIALIST |
| `phone` | String | Contact number |
| `address` | String | Physical address |
| `preferredPaymentMethod` | String | Payment preference (customers) |
| `title` | String | Professional title (specialists) |
| `services` | List<String> | Services offered (specialists) |
| `hourlyRate` | BigDecimal | Rate per hour (specialists) |
| `verified` | Boolean | Admin verification status (specialists) |

Inherits from `Auditable` for createdAt/updatedAt timestamps.

## API Endpoints

### Customers

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/customers` | List all customers |
| GET | `/api/v1/customers/{id}` | Get customer by ID |
| POST | `/api/v1/customers` | Create customer |
| PUT | `/api/v1/customers/{id}` | Update customer |
| DELETE | `/api/v1/customers/{id}` | Delete customer |

### Specialists

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/specialists` | List all specialists |
| GET | `/api/v1/specialists/{id}` | Get specialist by ID |
| GET | `/api/v1/specialists/by-service/{service}` | Find by service type |
| POST | `/api/v1/specialists` | Create specialist |
| PUT | `/api/v1/specialists/{id}` | Update specialist |
| DELETE | `/api/v1/specialists/{id}` | Delete specialist |

## Configuration

```yaml
# application.yml
server:
  port: 9001

spring:
  data:
    mongodb:
      uri: ${MONGODB_URI}

user:
  service:
    url: ${USER_SERVICE_URL:http://localhost:9001}

notification:
  service:
    url: ${NOTIFICATION_SERVICE_URL:http://localhost:9003}
```

## Key Classes

| Class | Purpose |
|-------|---------|
| `UserEntity` | MongoDB document model |
| `UserRepository` | Data access layer |
| `UserService` | Business logic |
| `UserMapper` | Entity/DTO conversion (MapStruct) |
| `CustomerController` | Customer REST endpoints |
| `SpecialistController` | Specialist REST endpoints |

## DTOs

Uses shared DTOs from `user-service-client` module:

- `CustomerRequest` / `CustomerResponse`
- `SpecialistRequest` / `SpecialistResponse`

## Integration

- **Notification Service**: Calls for email notifications on profile updates
- **Booking Service**: Uses `user-service-client` Feign client to fetch user data

## Database Indexes

```java
@CompoundIndex(name = "user_role_index", def = "{'role': 1}")
@CompoundIndex(name = "services_index", def = "{'services': 1}")
```

Indexes on `email` (unique) and `role` for efficient queries.

## Building

```bash
# Build this service only
./gradlew :backend:user-service:build

# Run tests
./gradlew :backend:user-service:test
```

## API Documentation

Swagger UI available at: `http://localhost:9001/swagger-ui.html`
