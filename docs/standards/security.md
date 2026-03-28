# Security Guidelines

Security best practices for SnapServe development.

## Authentication

### JWT Token Management

**Access Tokens:**
- Short-lived (15 minutes)
- Stored in memory only (never localStorage)
- Sent in Authorization header: `Bearer <token>`

**Refresh Tokens:**
- Long-lived (7 days)
- Stored in httpOnly cookie or secure storage
- Rotated on each use (new token issued)
- Single-use only (old token invalidated)

### Token Validation

Gateway validates tokens before routing. See `backend/api-gateway` for implementation.

### Password Security

**Requirements:**
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character (@$!%*?&)

**Hashing:**
- Use BCrypt with strength 10+
- Never store plain text passwords
- Implementation: See `backend/auth-service` PasswordEncoder configuration

### Account Lockout

Security feature to prevent brute force attacks:
- **Max attempts:** 5 failed logins
- **Lockout duration:** 30 minutes
- **Reset:** Successful login or admin unlock

Implementation: See `backend/auth-service` account security logic.

## Authorization

### Role-Based Access Control (RBAC)

**Roles:**
- `CUSTOMER` — Can book appointments, manage profile
- `SPECIALIST` — Can manage availability, view appointments
- `ADMIN` — Can approve specialists, manage users

### Endpoint Protection

**Controller Level:**
Use `@PreAuthorize` annotations:
- `@PreAuthorize("hasRole('CUSTOMER')")` — Customer only
- `@PreAuthorize("hasRole('SPECIALIST')")` — Specialist only
- `@PreAuthorize("hasRole('ADMIN')")` — Admin only

**Gateway Level:**
Public endpoints configured in gateway route validator.

### Data Ownership

Always verify users can only access their own data:
- Check user ID from JWT against resource owner
- Throw `AccessDeniedException` if not authorized
- Implementation: See `backend/booking-service` booking access control

## Input Validation

### Bean Validation

Validate all inputs with Bean Validation annotations:
- `@NotBlank` — String not null and not empty
- `@NotNull` — Value not null
- `@Email` — Valid email format
- `@Size` — String length constraints
- `@Pattern` — Regex validation

### Injection Prevention

**MongoDB:**
- Use Spring Data repositories (parameterized queries)
- Never concatenate user input into queries
- See repository interfaces in `backend/*/repository/`

**XSS Prevention:**
- Backend: Escape output in email templates
- Frontend: React automatically escapes JSX (good!)
- Never use `dangerouslySetInnerHTML`

## Secrets Management

### Environment Variables

**Never hardcode secrets:**
- JWT secret
- Database credentials
- Email credentials
- All stored in `.env` file

**Required environment variables:**
- `JWT_SECRET` — 64+ character secret
- `MONGODB_URI` — Database connection string
- `GMAIL_APP_PASSWORD` — Email service password

See `.env.example` for complete list.

### Secret Rotation

- Rotate JWT secrets periodically (every 90 days)
- Rotate database credentials
- Rotate third-party API keys
- Use different secrets per environment

## CORS Configuration

**Only at Gateway:**

Never use `@CrossOrigin` on controllers.

Configuration:
- Allowed origins: Via `ALLOWED_ORIGINS` env var
- Allowed methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
- Credentials: Allowed

Implementation: See `backend/api-gateway` CORS configuration.

## Rate Limiting

Implement rate limiting at gateway to prevent abuse:
- Default: 100 requests per minute per client
- Status 429 returned when exceeded
- Headers indicate limit status

Implementation: See `backend/api-gateway` rate limiting filter.

## Logging Security

### Don't Log Sensitive Data

❌ Never log:
- Passwords
- JWT tokens
- Credit card numbers
- Personal identifiable information (PII)

✅ Do log:
- User actions (without sensitive data)
- Error messages (sanitized)
- Request paths (without query params containing secrets)

### Mask Sensitive Data

When logging user information:
- Mask email: `j***n@example.com`
- Mask phone: `***-***-1234`
- Use partial masking for identification without exposure

## HTTPS/TLS

**Production:**
- Always use HTTPS
- TLS 1.2 or higher
- Valid SSL certificates
- HSTS headers

**Local Development:**
- HTTP acceptable for local dev
- Gateway handles HTTPS termination in production

## File Uploads

If implementing file uploads:

**Validation:**
- Validate file type (whitelist allowed types)
- Validate file size (max limit)
- Scan for malware

**Storage:**
- Store with UUID filename (prevent path traversal)
- Never use original filename directly
- Store outside web root

**Example validation:**
- Max size: 5MB
- Allowed types: image/jpeg, image/png
- Storage: UUID-based filenames

## Security Checklist

Before deploying:

- [ ] JWT secrets are strong (64+ chars) and rotated
- [ ] No hardcoded credentials in code
- [ ] All inputs validated (Bean Validation)
- [ ] All endpoints protected (except public ones)
- [ ] Passwords hashed with BCrypt
- [ ] Account lockout implemented
- [ ] CORS configured at gateway only
- [ ] Rate limiting enabled
- [ ] No sensitive data in logs
- [ ] HTTPS enabled in production
- [ ] Dependencies updated (no known vulnerabilities)

## Security Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

## Links

- JWT implementation: `backend/auth-service`
- Security filters: `backend/api-gateway`
- Validation examples: `backend/user-service/dto/`
