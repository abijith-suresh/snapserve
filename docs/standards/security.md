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

```java
// Gateway validates tokens before routing
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain) {
        String token = extractToken(request);
        if (token != null && jwtUtils.validateToken(token)) {
            Authentication auth = createAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }
}
```

### Password Security

**Backend:**
- Use BCrypt with strength 10+ for hashing
- Never store plain text passwords
- Implement account lockout after 5 failed attempts
- Require strong passwords (min 8 chars, mixed case, numbers, symbols)

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
}
```

**Validation:**
```java
@NotBlank(message = "Password is required")
@Size(min = 8, message = "Password must be at least 8 characters")
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
         message = "Password must contain uppercase, lowercase, number, and special character")
String password;
```

## Authorization

### Role-Based Access Control (RBAC)

```java
public enum Role {
    CUSTOMER,
    SPECIALIST,
    ADMIN
}
```

### Endpoint Protection

**Controller Level:**
```java
@PreAuthorize("hasRole('CUSTOMER')")
@PostMapping("/bookings")
public ResponseEntity<ApiResponse<BookingDto>> createBooking(...) { }

@PreAuthorize("hasRole('SPECIALIST')")
@GetMapping("/specialists/{id}/appointments")
public ResponseEntity<ApiResponse<List<BookingDto>>> getAppointments(...) { }
```

**Gateway Level:**
```java
// RouteValidator.java - Define public endpoints
public static final List<String> OPEN_ENDPOINTS = Arrays.asList(
    "/api/v1/auth/login",
    "/api/v1/auth/register",
    "/api/v1/auth/refresh"
);
```

### Data Ownership

Always verify users can only access their own data:

```java
@GetMapping("/bookings/{id}")
public ResponseEntity<ApiResponse<BookingDto>> getBooking(
        @PathVariable String id,
        @AuthenticationPrincipal UserDetails user) {
    
    Booking booking = bookingService.getBooking(id);
    
    // Verify ownership
    if (!booking.getCustomerId().equals(user.getId()) && 
        !booking.getSpecialistId().equals(user.getId())) {
        throw new AccessDeniedException("Not authorized to view this booking");
    }
    
    return ResponseEntity.ok(ApiResponse.success(booking));
}
```

## Input Validation

### Bean Validation

Validate all inputs with Bean Validation:

```java
public record CreateUserRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    String email,
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name contains invalid characters")
    String name
) {}
```

### SQL/NoSQL Injection Prevention

**Use Spring Data repositories:**
```java
// Safe - uses parameterized queries
@Query("{ 'email': ?0 }")
Optional<User> findByEmail(String email);

// NEVER do this - vulnerable to injection
@Query("{ 'email': '" + email + "' }")  // ❌ DON'T
```

### XSS Prevention

**Backend:**
- Escape all output in email templates
- Content-Type headers on all responses
- Validate and sanitize file uploads

**Frontend:**
- React automatically escapes JSX (good!)
- Don't use `dangerouslySetInnerHTML`
- Validate URLs before navigation

## Secrets Management

### Environment Variables

**Never hardcode secrets:**

```java
// ❌ DON'T
private static final String JWT_SECRET = "mysecretkey123";

// ✅ DO
@Value("${jwt.secret}")
private String jwtSecret;
```

**Required in .env:**
```bash
JWT_SECRET=your-64-character-secret-here-minimum...
MONGODB_URI=mongodb://localhost:27017/snapserve
GMAIL_APP_PASSWORD=your-app-password
```

### Secret Rotation

- Rotate JWT secrets periodically (every 90 days)
- Rotate database credentials
- Rotate third-party API keys
- Use different secrets per environment

## CORS Configuration

**Only at Gateway:**

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

**Never use @CrossOrigin on controllers.**

## Rate Limiting

Implement rate limiting at gateway:

```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain) {
        
        String clientId = getClientIdentifier(request);
        Bucket bucket = buckets.computeIfAbsent(clientId, this::createBucket);
        
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded");
        }
    }
    
    private Bucket createBucket(String key) {
        return Bucket.builder()
            .addLimit(limit -> limit.capacity(100).refillIntervally(1, Duration.ofMinutes(1)))
            .build();
    }
}
```

## Logging Security

**Don't log sensitive data:**

```java
// ❌ DON'T
log.info("User login: email={}, password={}", email, password);

// ✅ DO
log.info("User login attempt: email={}", email);
```

**Mask sensitive data:**

```java
public String maskEmail(String email) {
    if (email == null || !email.contains("@")) return email;
    String[] parts = email.split("@");
    String local = parts[0];
    String domain = parts[1];
    String maskedLocal = local.charAt(0) + "***" + local.charAt(local.length() - 1);
    return maskedLocal + "@" + domain;
}

// Usage
log.info("Password reset requested for: {}", maskEmail(email));
// Output: Password reset requested for: j***n@example.com
```

## HTTPS/TLS

**Production:**
- Always use HTTPS
- TLS 1.2 or higher
- Valid SSL certificates
- HSTS headers

**Local Development:**
- HTTP is acceptable for local dev
- Gateway handles HTTPS termination in production

## File Uploads

If implementing file uploads:

```java
@PostMapping("/upload")
public ResponseEntity<ApiResponse<String>> uploadFile(
        @RequestParam("file") MultipartFile file) {
    
    // Validate file type
    String contentType = file.getContentType();
    if (!Arrays.asList("image/jpeg", "image/png").contains(contentType)) {
        throw new BadRequestException("Invalid file type");
    }
    
    // Validate file size (max 5MB)
    if (file.getSize() > 5 * 1024 * 1024) {
        throw new BadRequestException("File too large");
    }
    
    // Store with UUID filename (prevent path traversal)
    String filename = UUID.randomUUID().toString() + "." + getExtension(file);
    
    // Save to storage...
}
```

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
