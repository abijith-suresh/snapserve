# Code Style Standards

This document defines the coding standards for both Java (backend) and TypeScript (frontend).

## Java Standards

### General Principles

- **Java 21** features (records, pattern matching, etc.)
- **Constructor injection** only (never `@Autowired` field injection)
- **Java Records** for all DTOs (not Lombok `@Data`)
- **MapStruct** for DTO mapping
- **Bean Validation** annotations on all DTO fields
- **Transactional** on service methods that modify data

### Class Structure

```java
package com.snapserve.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public UserDto createUser(CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
```

### DTO Pattern (Java Records)

```java
public record CreateUserRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    String name,
    
    @NotNull(message = "Role is required")
    Role role
) {}
```

**Never use Lombok `@Data` for DTOs.** Records provide:
- Immutability by default
- Built-in equals/hashCode/toString
- Compact syntax
- Better type safety

### Controller Pattern

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserDto user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable String id) {
        UserDto user = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
```

### Entity Pattern

```java
@Data
@Document(collection = "users")
public class User extends Auditable {
    
    @Id
    private ObjectId id;
    
    @Indexed(unique = true)
    private String email;
    
    private String name;
    
    @Enumerated(EnumType.STRING)
    private Role role;
}
```

**Note:** Entities use Lombok `@Data` (they're mutable), but DTOs use Records.

### Repository Pattern

```java
@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRoleAndActiveTrue(Role role);
}
```

### MapStruct Mapper

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDto toDto(User user);
    
    User toEntity(CreateUserRequest request);
    
    void updateEntity(@MappingTarget User user, UpdateUserRequest request);
}
```

### Exception Handling

Use the `GlobalExceptionHandler` from `common` module:

```java
// In service
public User getUser(String id) {
    return userRepository.findById(new ObjectId(id))
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
}

// Don't catch and return null - always throw exceptions
```

### Naming Conventions

- **Classes**: PascalCase (`UserService`, `CreateUserRequest`)
- **Methods**: camelCase (`createUser`, `findByEmail`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_RETRY_COUNT`)
- **Packages**: lowercase (`com.snapserve.userservice`)
- **DTOs**: Suffix with type (`CreateUserRequest`, `UserDto`, `UpdateUserRequest`)
- **Repositories**: Suffix with `Repository` (`UserRepository`)
- **Services**: Suffix with `Service` (`UserService`)
- **Controllers**: Suffix with `Controller` (`UserController`)
- **Mappers**: Suffix with `Mapper` (`UserMapper`)

### Formatting

Use **Spotless** with the included configuration:

```bash
./gradlew spotlessCheck   # Check formatting
./gradlew spotlessApply   # Fix formatting
```

- 4 spaces indentation
- No wildcard imports
- Import order: java, javax, org, com, static
- No trailing whitespace

## TypeScript Standards

### General Principles

- **Strict mode** enabled (no implicit any, strict null checks, etc.)
- **Explicit types** on function parameters and return types
- **Interface** for object shapes, **Type** for unions/aliases
- **const/let** (never var)
- **async/await** for async operations (no raw promises)

### Component Structure

```typescript
// features/users/UserList.tsx
import { useQuery } from '@tanstack/react-query';
import { getUsers } from '@/shared/api/users';

interface UserListProps {
  role: 'customer' | 'specialist';
}

export function UserList({ role }: UserListProps): JSX.Element {
  const { data: users, isLoading, error } = useQuery({
    queryKey: ['users', role],
    queryFn: () => getUsers(role),
  });

  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;
  if (!users?.length) return <EmptyState />;

  return (
    <ul className="space-y-2">
      {users.map(user => (
        <li key={user.id} className="p-4 border rounded">
          {user.name}
        </li>
      ))}
    </ul>
  );
}
```

### Custom Hooks

```typescript
// hooks/useUser.ts
import { useQuery } from '@tanstack/react-query';
import { getUser } from '@/shared/api/users';

interface UseUserOptions {
  enabled?: boolean;
}

export function useUser(userId: string, options: UseUserOptions = {}) {
  return useQuery({
    queryKey: ['user', userId],
    queryFn: () => getUser(userId),
    enabled: options.enabled ?? true,
  });
}
```

### API Client Pattern

```typescript
// shared/api/client.ts
import axios from 'axios';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:9090',
});

apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Handle token refresh or logout
    }
    return Promise.reject(error);
  }
);
```

### Zustand Store Pattern

```typescript
// features/auth/store.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  login: (user: User) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    set => ({
      user: null,
      isAuthenticated: false,
      login: user => set({ user, isAuthenticated: true }),
      logout: () => set({ user: null, isAuthenticated: false }),
    }),
    { name: 'auth-storage' }
  )
);
```

### Form Handling

```typescript
// features/auth/LoginForm.tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const loginSchema = z.object({
  email: z.string().email('Invalid email'),
  password: z.string().min(8, 'Password must be 8+ characters'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export function LoginForm(): JSX.Element {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData): Promise<void> => {
    // Handle login
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input {...register('email')} />
      {errors.email && <span>{errors.email.message}</span>}
      
      <input type="password" {...register('password')} />
      {errors.password && <span>{errors.password.message}</span>}
      
      <button type="submit">Login</button>
    </form>
  );
}
```

### Naming Conventions

- **Components**: PascalCase (`UserList`, `LoginForm`)
- **Hooks**: camelCase with `use` prefix (`useUser`, `useAuth`)
- **Types/Interfaces**: PascalCase (`User`, `ApiResponse`)
- **Functions**: camelCase (`getUser`, `handleSubmit`)
- **Constants**: UPPER_SNAKE_CASE (`API_URL`, `MAX_RETRIES`)
- **Files**: Match default export name (`UserList.tsx` exports `UserList`)
- **Boolean props**: Prefix with verb (`isLoading`, `hasError`, `canEdit`)

### Formatting

Use **Biome** for linting and formatting:

```bash
bun run lint          # Check linting
bun run lint:fix      # Fix auto-fixable issues
bun run format        # Format code
```

- 2 spaces indentation
- Single quotes for strings
- Semicolons required
- Trailing commas (multi-line)

## Import Organization

### Java

```java
// Standard library
import java.time.LocalDateTime;
import java.util.Optional;

// Spring
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Project
import com.snapserve.common.model.Auditable;
import com.snapserve.service.dto.UserDto;
```

### TypeScript

```typescript
// External libraries
import { useQuery } from '@tanstack/react-query';
import { z } from 'zod';

// Internal absolute imports (using @ alias)
import { apiClient } from '@/shared/api/client';
import { useAuthStore } from '@/features/auth/store';

// Internal relative imports
import { UserCard } from './UserCard';
```

## Documentation

### Java

- **Public APIs**: JavaDoc for public methods
- **Complex logic**: Inline comments explaining "why", not "what"
- **TODOs**: Use `// TODO: description` format

### TypeScript

- **Public functions**: TSDoc for exported functions
- **Complex types**: Explain purpose in comments
- **TODOs**: Use `// TODO: description` format

## Code Review Checklist

Before submitting PR:

- [ ] All tests pass
- [ ] No TypeScript errors (`bun run typecheck`)
- [ ] Java formatting passes (`./gradlew spotlessCheck`)
- [ ] No console.log statements (except in dev)
- [ ] Proper error handling implemented
- [ ] Types are explicit (no `any`)
- [ ] Follows naming conventions
- [ ] DTOs use Java Records (not Lombok @Data)
- [ ] Constructor injection used (no field injection)
- [ ] ApiResponse wrapper used for all endpoints
