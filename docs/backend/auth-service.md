# Auth Service

Handles user authentication, JWT token management, and account security.

## Responsibilities

- User registration (customers and specialists)
- Login/logout with JWT tokens
- Token refresh
- Account lockout after failed login attempts
- Password hashing with BCrypt

## Authentication Flow

```
1. Register
   POST /api/v1/auth/register
   Request: { email, password, name, role }
   Response: 201 Created

2. Login
   POST /api/v1/auth/login
   Request: { email, password }
   Response: { accessToken, refreshToken, expiresIn }

3. Access Protected Resource
   GET /api/v1/customers/123
   Header: Authorization: Bearer <accessToken>

4. Refresh Token (when access token expires)
   POST /api/v1/auth/refresh
   Request: { refreshToken }
   Response: { accessToken, refreshToken, expiresIn }

5. Logout
   POST /api/v1/auth/logout
   Header: Authorization: Bearer <accessToken>
```

## API Endpoints

### POST /api/v1/auth/register

Register a new customer or specialist.

**Request:**
```json
{
  "email": "customer@example.com",
  "password": "SecurePass123!",
  "name": "John Doe",
  "role": "CUSTOMER"
}
```

**Response 201:**
```json
{
  "success": true,
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "email": "customer@example.com",
    "name": "John Doe",
    "role": "CUSTOMER",
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "message": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Validation:**
- Email: Required, valid format, unique
- Password: Min 8 chars, uppercase, lowercase, number, special char
- Name: 2-100 characters
- Role: CUSTOMER or SPECIALIST

### POST /api/v1/auth/login

Authenticate user and return tokens.

**Request:**
```json
{
  "email": "customer@example.com",
  "password": "SecurePass123!"
}
```

**Response 200:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g...",
    "expiresIn": 900
  },
  "message": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Error Responses:**
- 400: Invalid credentials
- 403: Account locked (after 5 failed attempts)
- 404: User not found

### POST /api/v1/auth/refresh

Get new access token using refresh token.

**Request:**
```json
{
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g..."
}
```

**Response 200:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "newRefreshToken...",
    "expiresIn": 900
  },
  "message": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Note:** Refresh token rotation - new refresh token issued, old one invalidated.

### POST /api/v1/auth/logout

Invalidate refresh token.

**Headers:**
- Authorization: Bearer <accessToken>

**Response 200:**
```json
{
  "success": true,
  "data": null,
  "message": "Logged out successfully",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Token Details

### Access Token

- **Type:** JWT
- **Algorithm:** HS256
- **Lifetime:** 15 minutes (900 seconds)
- **Storage:** Memory only (React state)
- **Claims:**
  - `sub`: User ID
  - `email`: User email
  - `role`: User role (CUSTOMER, SPECIALIST, ADMIN)
  - `iat`: Issued at
  - `exp`: Expiration

### Refresh Token

- **Type:** JWT
- **Algorithm:** HS256
- **Lifetime:** 7 days
- **Storage:** httpOnly cookie or secure storage
- **Single-use:** Yes (rotated on each use)
- **Claims:**
  - `sub`: User ID
  - `type`: "refresh"
  - `jti`: Unique token ID (for revocation)

## Account Lockout

Security feature to prevent brute force attacks:

- **Max attempts:** 5 failed logins
- **Lockout duration:** 30 minutes
- **Reset:** Successful login or admin unlock

```java
@Entity
public class Account extends Auditable {
    private int failedLoginAttempts = 0;
    private LocalDateTime lockedUntil;
    
    public boolean isLocked() {
        if (lockedUntil == null) return false;
        return LocalDateTime.now().isBefore(lockedUntil);
    }
}
```

## Password Security

**Hashing:** BCrypt with strength 10

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
}
```

**Validation:**
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character (@$!%*?&)

## Architecture

```
AccountController
    ├── AccountService
    │       ├── AccountRepository (MongoDB)
    │       ├── PasswordEncoder (BCrypt)
    │       └── JwtTokenProvider
    └── DTOs (Java Records)
            ├── RegisterRequest
            ├── LoginRequest
            ├── RefreshRequest
            └── AuthResponse
```

## Key Classes

### AccountController

```java
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AccountController {
    
    private final AccountService accountService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AccountDto>> register(
            @Valid @RequestBody RegisterRequest request) { }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) { }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshRequest request) { }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) { }
}
```

### DTOs (Java Records)

```java
public record RegisterRequest(
    @NotBlank @Email @Size(max = 255) String email,
    @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$") String password,
    @NotBlank @Size(min = 2, max = 100) String name,
    @NotNull Role role
) {}

public record LoginRequest(
    @NotBlank @Email String email,
    @NotBlank String password
) {}

public record RefreshRequest(
    @NotBlank String refreshToken
) {}

public record AuthResponse(
    String accessToken,
    String refreshToken,
    long expiresIn
) {}
```

## Configuration

### application.yml

```yaml
server:
  port: 9000

spring:
  application:
    name: auth-service
  data:
    mongodb:
      uri: ${MONGODB_URI}
      auto-index-creation: true

jwt:
  secret: ${JWT_SECRET}
  access-token-expiration: 900000      # 15 minutes
  refresh-token-expiration: 604800000  # 7 days

logging:
  level:
    com.snapserve.authservice: INFO
```

## Dependencies

```kotlin
dependencies {
    implementation(project(":backend:common"))
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")
    
    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
}
```

## Database Schema

**Collection:** `accounts`

```javascript
{
  _id: ObjectId,
  email: String (unique, indexed),
  password: String (bcrypt hash),
  name: String,
  role: String (CUSTOMER | SPECIALIST | ADMIN),
  failedLoginAttempts: Number,
  lockedUntil: Date,
  createdAt: Date,
  updatedAt: Date
}
```

Indexes:
- `email`: Unique
- `role`: For filtering by role

## Links

- [API Design Standards](../standards/api-design.md)
- [Security Guidelines](../standards/security.md)
- [User Service](./user-service.md) — Profile management
