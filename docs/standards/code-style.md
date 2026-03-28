# Code Style Standards

Guidelines for Java (backend) and TypeScript (frontend) development.

## Java Standards

### Core Principles

- **Java 21** — Use modern features (records, pattern matching)
- **Constructor injection only** — Never use `@Autowired` field injection
- **Java Records for DTOs** — Immutable data transfer objects (not Lombok `@Data`)
- **MapStruct for mapping** — Automatic DTO conversion
- **Bean Validation** — All DTO fields must have validation annotations
- **@Transactional** — Service methods that modify data must be transactional

### Patterns

**DTOs**: Use Java Records with validation annotations
- Location: `src/main/java/.../dto/`
- Example implementations: See `backend/user-service/src/main/java/.../dto/`

**Controllers**: REST endpoints with standard patterns
- Location: `src/main/java/.../controller/`
- Example: `UserController.java` in user-service

**Services**: Business logic with transaction boundaries
- Location: `src/main/java/.../service/`
- Use `@RequiredArgsConstructor` for dependency injection

**Repositories**: Spring Data MongoDB
- Location: `src/main/java/.../repository/`
- Extend `MongoRepository<Entity, ObjectId>`

**Mappers**: MapStruct interfaces for DTO conversion
- Location: `src/main/java/.../mapper/`
- Example: `UserMapper.java` in user-service

### Naming Conventions

- **Classes**: PascalCase (`UserService`, `CreateUserRequest`)
- **Methods**: camelCase (`createUser`, `findByEmail`)
- **Constants**: UPPER_SNAKE_CASE
- **Packages**: lowercase (`com.snapserve.userservice`)
- **DTOs**: Suffix with type (`CreateUserRequest`, `UserDto`)
- **Repositories**: Suffix with `Repository`
- **Services**: Suffix with `Service`
- **Controllers**: Suffix with `Controller`
- **Mappers**: Suffix with `Mapper`

### Code Quality Tools

**Spotless** — Enforces formatting
```bash
./gradlew spotlessCheck   # Verify formatting
./gradlew spotlessApply   # Fix formatting
```

- 4 spaces indentation
- No wildcard imports
- Import order: java, javax, org, com, static

### Project Structure

Each service follows this structure:
```
src/main/java/com/snapserve/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── dto/            # Request/response DTOs (Records)
├── mapper/         # MapStruct mappers
├── model/          # Entity classes
└── config/         # Configuration classes
```

## TypeScript Standards

### Core Principles

- **Strict mode** — Full TypeScript strictness (no implicit any, strict null checks)
- **Explicit types** — Function parameters and return types must be typed
- **Interface for objects** — Use interfaces for object shapes
- **Type for unions** — Use type aliases for unions/complex types
- **const/let only** — Never use `var`
- **async/await** — For all asynchronous operations

### Patterns

**Components**: React functional components with TypeScript
- Location: `src/features/` or `src/components/`
- Example: `src/features/customer/CustomerDashboard.tsx`

**Custom Hooks**: Reusable logic
- Location: `src/hooks/`
- Prefix with `use`: `useUser`, `useAuth`

**State Management**:
- **Client state**: Zustand stores in `src/features/<feature>/store.ts`
- **Server state**: React Query hooks in `src/features/<feature>/queries.ts`

**API Client**: Axios configuration
- Location: `src/shared/api/client.ts`
- Base URL configured via environment variable

**Forms**: React Hook Form + Zod validation
- Define schemas in `src/shared/types/` or feature folders
- Example: Login form validation

### Naming Conventions

- **Components**: PascalCase (`UserList`, `LoginForm`)
- **Hooks**: camelCase with `use` prefix (`useUser`, `useAuth`)
- **Types/Interfaces**: PascalCase (`User`, `ApiResponse`)
- **Functions**: camelCase (`getUser`, `handleSubmit`)
- **Constants**: UPPER_SNAKE_CASE (`API_URL`, `MAX_RETRIES`)
- **Files**: Match default export name (`UserList.tsx` exports `UserList`)
- **Boolean props**: Prefix with verb (`isLoading`, `hasError`, `canEdit`)

### Code Quality Tools

**Biome** — Linting and formatting
```bash
bun run lint       # Check linting
bun run lint:fix   # Fix auto-fixable issues
bun run format     # Format code
```

- 2 spaces indentation
- Single quotes
- Semicolons required
- Trailing commas (multi-line)

### Project Structure

```
src/
├── components/ui/     # shadcn/ui components
├── features/          # Feature modules
│   ├── auth/         # Auth store
│   ├── customer/     # Customer portal
│   └── specialist/   # Specialist portal
├── pages/            # Public pages
├── routes/           # Route layouts
├── shared/           # Shared resources
│   ├── api/         # Axios client
│   └── types/       # TypeScript types
├── hooks/            # Custom React hooks
└── lib/              # Utilities
```

## Documentation

### Comments

**Java**:
- Public APIs: JavaDoc for public methods
- Complex logic: Inline comments explaining "why", not "what"
- Use `// TODO: description` format

**TypeScript**:
- Public functions: TSDoc for exported functions
- Complex types: Explain purpose in comments
- Use `// TODO: description` format

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

## Links

- Backend examples: See `backend/user-service/src/main/java/` (reference implementation)
- Frontend examples: See `frontend/src/features/` 
