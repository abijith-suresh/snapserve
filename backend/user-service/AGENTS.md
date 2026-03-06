# User Service Agent Context

User profile management for customers and specialists. Port: 9001.

## Quick Commands

```bash
# Build
./gradlew :backend:user-service:build

# Test
./gradlew :backend:user-service:test

# Docker
docker compose up -d user-service

# Test endpoint
curl http://localhost:9001/api/v1/specialists
```

## Key Files

| File | Purpose |
|------|---------|
| `UserServiceApplication.java` | Entry point |
| `controller/CustomerController.java` | Customer CRUD |
| `controller/SpecialistController.java` | Specialist CRUD |
| `service/UserService.java` | Business logic |
| `model/UserEntity.java` | MongoDB document |
| `repo/UserRepository.java` | Data access |
| `mapper/UserMapper.java` | Entity/DTO mapping |

## API Endpoints

**Customers:**
- `GET /api/v1/customers` - List all
- `GET /api/v1/customers/{id}` - Get by ID
- `POST /api/v1/customers` - Create
- `PUT /api/v1/customers/{id}` - Update
- `DELETE /api/v1/customers/{id}` - Delete

**Specialists:**
- `GET /api/v1/specialists` - List all
- `GET /api/v1/specialists/{id}` - Get by ID
- `GET /api/v1/specialists/by-service/{service}` - Filter by service
- `POST /api/v1/specialists` - Create
- `PUT /api/v1/specialists/{id}` - Update
- `DELETE /api/v1/specialists/{id}` - Delete

## UserEntity Model

Single collection `users` with role-specific fields:

```java
@Document(collection = "users")
public class UserEntity extends Auditable {
    private String email;        // Unique
    private String name;
    private Role role;           // CUSTOMER or SPECIALIST
    private String phone;
    private String address;
    
    // Customer-only
    private String preferredPaymentMethod;
    
    // Specialist-only
    private String title;
    private List<String> services;
    private BigDecimal hourlyRate;
    private Boolean verified;
}
```

## Service-Specific Rules

1. **Single table**: Both roles in `users` collection.
2. **Indexes**: `email` (unique), `role`, `services`.
3. **No auth logic**: JWT validated at gateway.
4. **Notification calls**: Call notification-service for emails.
5. **Use shared DTOs**: From `user-service-client` module.
6. **MapStruct**: Auto-generate mappers.

## Environment Variables

```yaml
MONGODB_URI: MongoDB connection string
NOTIFICATION_SERVICE_URL: http://notification-service:9003
```

## Full Documentation

- [User Service Docs](../../docs/backend/user-service.md)
- [Architecture Overview](../../AGENTS.md)

## Port

9001
