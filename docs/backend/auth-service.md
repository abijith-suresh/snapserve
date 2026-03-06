# Auth Service

Handles user authentication, JWT token management, and account security.

## Purpose

Manages identity and access:
- User registration (customers and specialists)
- Login/logout with JWT tokens
- Token refresh
- Account lockout security
- Password management

## API Endpoints

### POST /api/v1/auth/register

Register a new customer or specialist.

**Access**: Public (no authentication required)

**Validation:**
- Email: Required, valid format, unique
- Password: Min 8 chars, uppercase, lowercase, number, special char
- Name: 2-100 characters
- Role: CUSTOMER or SPECIALIST

**Response**: 201 Created with user details

### POST /api/v1/auth/login

Authenticate user and return tokens.

**Access**: Public

**Request:**
- email (string)
- password (string)

**Success Response:**
- accessToken (string) — Valid for 15 minutes
- refreshToken (string) — Valid for 7 days
- expiresIn (number) — Seconds until access token expiry

**Error Responses:**
- 400: Invalid credentials
- 403: Account locked (after 5 failed attempts)
- 404: User not found

### POST /api/v1/auth/refresh

Get new access token using refresh token.

**Access**: Public

**Request:**
- refreshToken (string)

**Success Response:**
- accessToken (string)
- refreshToken (string) — New token (rotation)
- expiresIn (number)

**Note:** Refresh tokens are single-use. New token issued, old one invalidated.

### POST /api/v1/auth/logout

Invalidate refresh token.

**Access**: Protected (requires valid access token)

**Headers:**
- Authorization: Bearer <accessToken>

**Response**: 200 OK with success message

## Token Details

### Access Token

- **Lifetime**: 15 minutes
- **Storage**: Memory only (React state)
- **Transport**: Authorization header
- **Contains**: User ID, email, role

### Refresh Token

- **Lifetime**: 7 days
- **Storage**: httpOnly cookie or secure storage
- **Single-use**: Yes (rotated on each refresh)
- **Purpose**: Obtain new access token without re-login

## Security Features

### Account Lockout

Prevents brute force attacks:
- **Max attempts**: 5 failed logins
- **Lockout duration**: 30 minutes
- **Reset**: Successful login or admin unlock

After 5 failed attempts, account is locked for 30 minutes.

### Password Security

**Requirements:**
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character (@$!%*?&)

**Storage:**
- Hashed with BCrypt (strength 10)
- Never stored in plain text
- One-way hash (cannot recover original password)

## Architecture

### Components

**AccountController**
- REST endpoints for auth operations
- Request validation
- Response formatting

**AccountService**
- Business logic for authentication
- Token generation and validation
- Account lockout management
- Password hashing

**AccountRepository**
- Database access for account records
- Queries by email, ID

**DTOs** (Java Records)
- RegisterRequest — Registration input
- LoginRequest — Login input
- RefreshRequest — Token refresh input
- AuthResponse — Token output

### Authentication Flow

1. User submits credentials
2. System validates input format
3. System retrieves account by email
4. System verifies password with BCrypt
5. System checks account lock status
6. System generates JWT tokens
7. System returns tokens to client

## Database

**Collection**: `accounts`

Fields:
- `_id` — MongoDB ObjectId
- `email` — Unique email address
- `password` — BCrypt hashed password
- `name` — User display name
- `role` — CUSTOMER, SPECIALIST, or ADMIN
- `failedLoginAttempts` — Count for lockout
- `lockedUntil` — Lockout expiry timestamp
- `createdAt` — Account creation time
- `updatedAt` — Last update time

Indexes:
- `email`: Unique index for fast lookup

## Dependencies

- Spring Boot Web — REST endpoints
- Spring Data MongoDB — Database access
- Spring Security — Password encoding
- JJWT — Token generation and parsing
- Spring Validation — Input validation
- SpringDoc OpenAPI — API documentation

See `backend/auth-service/build.gradle.kts` for versions.

## Configuration

Key settings in `application.yml`:

```yaml
jwt:
  secret: ${JWT_SECRET}
  access-token-expiration: 900000      # 15 minutes (ms)
  refresh-token-expiration: 604800000  # 7 days (ms)
```

Environment variables:
- `JWT_SECRET` — 64+ character secret key
- `MONGODB_URI` — Database connection string

## API Documentation

OpenAPI/Swagger documentation available at:
`http://localhost:9090/swagger-ui.html`

## Links

- Service source: `backend/auth-service/`
- DTOs: `backend/auth-service/src/main/java/.../dto/`
- Controllers: `backend/auth-service/src/main/java/.../controller/`
- API design standards: [../standards/api-design.md](../standards/api-design.md)
- Security guidelines: [../standards/security.md](../standards/security.md)
