# API Gateway

The API Gateway is the entry point for all client requests. It handles routing, authentication, CORS, and rate limiting.

## Responsibilities

- **Routing**: Direct requests to appropriate microservices
- **Authentication**: Validate JWT tokens on protected routes
- **Authorization**: Check user roles for endpoint access
- **CORS**: Handle cross-origin requests from frontend
- **Rate Limiting**: Prevent abuse

## Architecture

```
Client Request
    │
    ▼
CORS Filter
    │
    ▼
JWT Authentication Filter
    │
    ▼
Role Authorization Filter
    │
    ▼
Route to Service
```

## Key Components

### 1. Route Configuration

Routes defined in `application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: http://auth-service:9000
          predicates:
            - Path=/api/v1/auth/**
          
        - id: user-service
          uri: http://user-service:9001
          predicates:
            - Path=/api/v1/customers/**, /api/v1/specialists/**, /api/v1/admins/**
          
        - id: booking-service
          uri: http://booking-service:9002
          predicates:
            - Path=/api/v1/bookings/**, /api/v1/reviews/**
```

### 2. JWT Validation Filter

Validates Bearer tokens on incoming requests:

```java
@Component
public class JwtAuthenticationFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        
        // Skip auth for open endpoints
        if (isOpenEndpoint(path)) {
            return chain.filter(exchange);
        }
        
        String token = extractToken(exchange);
        if (token == null || !jwtUtils.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        // Add user info to headers for downstream services
        exchange.getRequest().mutate()
            .header("X-User-Id", jwtUtils.extractUserId(token))
            .header("X-User-Role", jwtUtils.extractRole(token))
            .build();
        
        return chain.filter(exchange);
    }
}
```

### 3. Open Endpoints

These endpoints don't require authentication:

```java
public static final List<String> OPEN_ENDPOINTS = Arrays.asList(
    "/api/v1/auth/login",
    "/api/v1/auth/register",
    "/api/v1/auth/refresh"
);
```

### 4. Role-Based Access

Verifies users have required roles:

```java
private boolean hasRequiredRole(String roles, String path) {
    List<String> roleList = Arrays.asList(roles.split(","));
    
    if (path.startsWith("/api/v1/customers")) {
        return roleList.contains("CUSTOMER");
    }
    if (path.startsWith("/api/v1/specialists")) {
        return roleList.contains("SPECIALIST");
    }
    if (path.startsWith("/api/v1/admins")) {
        return roleList.contains("ADMIN");
    }
    return true;
}
```

## CORS Configuration

CORS is configured **only** at gateway:

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}
```

**Important:** Never use `@CrossOrigin` on downstream controllers.

## Configuration

### application.yml

```yaml
server:
  port: 9090

spring:
  application:
    name: api-gateway
  
  cloud:
    gateway:
      discovery:
        locator:
          enabled: false  # No Eureka
      routes:
        # Auth service
        - id: auth-service
          uri: http://auth-service:9000
          predicates:
            - Path=/api/v1/auth/**
        
        # User service
        - id: user-service
          uri: http://user-service:9001
          predicates:
            - Path=/api/v1/customers/**,/api/v1/specialists/**,/api/v1/admins/**
          filters:
            - JwtAuthFilter
        
        # Booking service
        - id: booking-service
          uri: http://booking-service:9002
          predicates:
            - Path=/api/v1/bookings/**,/api/v1/reviews/**
          filters:
            - JwtAuthFilter
        
        # Notification service
        - id: notification-service
          uri: http://notification-service:9003
          predicates:
            - Path=/api/v1/notifications/**
          filters:
            - JwtAuthFilter

jwt:
  secret: ${JWT_SECRET}

cors:
  allowed-origins: ${ALLOWED_ORIGINS:http://localhost:3000}
```

## Request Flow

### 1. Public Request

```
POST /api/v1/auth/login
    │
    ▼
CORS Check
    │
    ▼
Skip JWT (open endpoint)
    │
    ▼
Route to auth-service:9000
```

### 2. Protected Request

```
GET /api/v1/customers/123
Authorization: Bearer <token>
    │
    ▼
CORS Check
    │
    ▼
Validate JWT Token
    │
    ▼
Extract User ID and Role
    │
    ▼
Check Role (CUSTOMER required)
    │
    ▼
Add X-User-Id, X-User-Role headers
    │
    ▼
Route to user-service:9001
```

## Error Handling

### Invalid Token

```json
{
  "success": false,
  "data": null,
  "message": "Invalid or expired token",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Insufficient Permissions

```json
{
  "success": false,
  "data": null,
  "message": "Access denied. Required role: CUSTOMER",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Service Unavailable

```json
{
  "success": false,
  "data": null,
  "message": "Service temporarily unavailable",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Dependencies

```kotlin
dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("io.jsonwebtoken:jjwt-api")
    runtimeOnly("io.jsonwebtoken:jjwt-impl")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson")
}
```

## Testing

### Local Testing

```bash
# Start services
docker compose up

# Test public endpoint
curl http://localhost:9090/api/v1/auth/login \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password"}'

# Test protected endpoint (without token)
curl http://localhost:9090/api/v1/customers/123
# Should return 401

# Test protected endpoint (with token)
curl http://localhost:9090/api/v1/customers/123 \
  -H "Authorization: Bearer <valid-token>"
```

## Best Practices

1. **Keep gateway lean**: No business logic, just routing and auth
2. **Stateless**: No session storage in gateway
3. **Validate early**: Reject bad requests before routing
4. **Log minimally**: Don't log sensitive headers
5. **Health checks**: Implement `/actuator/health` endpoint
6. **Time out**: Set reasonable timeouts for upstream services

## Links

- [Architecture Overview](../architecture/overview.md)
- [Service Interactions](../architecture/service-interactions.md)
- [Security Guidelines](../standards/security.md)
