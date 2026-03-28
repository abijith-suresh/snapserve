# User Service

Manages customer, specialist, and admin profiles.

## Purpose

Handles user profile data:
- Customer profile CRUD operations
- Specialist profile with skills and availability
- Admin user management
- Specialist approval workflow

## API Endpoints

### Customer Endpoints

**GET /api/v1/customers/{id}**
- Get customer profile by ID
- Access: Customer (own profile) or Admin

**PUT /api/v1/customers/{id}**
- Update customer profile
- Access: Customer (own profile only)

**GET /api/v1/customers**
- List all customers (Admin only)
- Supports pagination

### Specialist Endpoints

**GET /api/v1/specialists/{id}**
- Get specialist profile by ID
- Access: Public (for browsing) or owner

**PUT /api/v1/specialists/{id}**
- Update specialist profile
- Access: Specialist (own profile only)

**GET /api/v1/specialists**
- List specialists with filters
- Query params: category, available, rating
- Access: Public

**POST /api/v1/specialists/{id}/approve**
- Approve specialist account
- Access: Admin only

### Admin Endpoints

**GET /api/v1/admins/users**
- List all users (all roles)
- Access: Admin only

**DELETE /api/v1/admins/users/{id}**
- Delete user account
- Access: Admin only

## Data Models

### Customer

Profile information for service customers:
- Basic info: name, email, phone
- Address: for service location
- Preferences: communication preferences

### Specialist

Profile for service providers:
- Basic info: name, email, phone
- Professional: skills, categories, experience
- Availability: working hours, service area
- Status: PENDING_APPROVAL, APPROVED, SUSPENDED
- Ratings: average rating, review count

### Admin

Administrator accounts:
- Basic info: name, email
- Permissions: full system access

## Specialist Approval Workflow

1. **Registration**: Specialist signs up via auth service
2. **Initial Status**: Account created with PENDING_APPROVAL
3. **Profile Setup**: Specialist completes profile with skills/credentials
4. **Admin Review**: Admin reviews application
5. **Approval**: Admin approves, status changes to APPROVED
6. **Visibility**: Specialist appears in search results

## Architecture

### Components

**CustomerController / SpecialistController / AdminController**
- REST endpoints for each user type
- Role-based access control
- Request validation

**UserService**
- Business logic for profile management
- Transaction boundaries
- Data validation

**UserRepository / CustomerRepository / SpecialistRepository**
- Data access layer
- Custom queries for filtering

**DTOs** (Java Records)
- Request/response objects for each operation
- Validation annotations

**Mapper** (MapStruct)
- Entity to DTO conversion
- DTO to Entity conversion

## Database

**Collections**:
- `customers` — Customer profiles
- `specialists` — Specialist profiles
- `admins` — Admin accounts

Each extends `Auditable` base class for createdAt/updatedAt.

## Feign Client Integration

Other services fetch user data via Feign Client:

**Booking Service uses:**
- Get customer by ID
- Get specialist by ID
- Validate user existence

Client defined in: `backend/user-service-client/`

## Dependencies

- Spring Boot Web
- Spring Data MongoDB
- Spring Validation
- MapStruct (DTO mapping)
- SpringDoc OpenAPI
- Common module (shared code)

See `build.gradle.kts` for complete list.

## Configuration

Environment variables:
- `MONGODB_URI` — Database connection
- `NOTIFICATION_SERVICE_URL` — For sending emails

## Reference Implementation

This service serves as the **reference implementation** for other backend services:

- Java Records for DTOs
- MapStruct for mapping
- Constructor injection
- Proper validation
- ApiResponse wrapper
- Transactional boundaries

Use this service as a template when building other services.

## Links

- Service source: `backend/user-service/`
- Feign client: `backend/user-service-client/`
- DTO examples: `backend/user-service/src/main/java/.../dto/`
- Controller examples: `backend/user-service/src/main/java/.../controller/`
- Code style: [../standards/code-style.md](../standards/code-style.md)
