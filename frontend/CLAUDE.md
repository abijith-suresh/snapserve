# Frontend Agent Context

React 18 + TypeScript SPA. Port: 3000 (Vite dev server).

## Quick Commands

```bash
# Install dependencies
bun install

# Development server
bun run dev

# Type check
bun run type-check

# Lint
bun run lint
bun run lint:fix

# Format
bun run format

# Build
bun run build

# Preview production build
bun run preview
```

## Key Files

| File | Purpose |
|------|---------|
| `src/App.tsx` | Router and app structure |
| `src/main.tsx` | React entry point |
| `src/routes/ProtectedRoute.tsx` | Role-based auth guard |
| `src/routes/RootLayout.tsx` | Public page layout |
| `src/features/auth/store.ts` | Zustand auth store |
| `src/shared/api/client.ts` | Axios API client |
| `src/shared/types/index.ts` | TypeScript types |

## Tech Stack

- React 18 + TypeScript (strict)
- Vite 7 (build tool)
- Bun (package manager)
- Tailwind CSS 4 (styling)
- React Router 7 (routing)
- Zustand (state management)
- React Query 5 (data fetching)
- shadcn/ui + Radix (components)

## Directory Structure

```
src/
├── components/        # shadcn/ui components
├── features/          # Domain modules
│   ├── auth/         # Auth store
│   ├── customer/     # Customer views
│   └── specialist/   # Specialist views
├── hooks/            # Custom hooks
├── lib/              # Utilities (utils.ts)
├── pages/            # Page components
├── routes/           # Route components
└── shared/           # Shared code
    ├── api/          # API client
    └── types/        # Type definitions
```

## Routing

Role-based routing with protection:

```
/                          - Home (public)
/login                     - Login (public)
/signup                    - Signup (public)
/specialists               - Browse (public)
/specialists/:id           - Profile (public)
/customer/*                - Customer area (protected)
/specialist/*              - Specialist area (protected)
```

## State Management

**Zustand** for auth:
```typescript
const { user, isAuthenticated, login, logout } = useAuthStore()
```

**React Query** for server data:
```typescript
const { data, isLoading } = useQuery({
  queryKey: ['bookings'],
  queryFn: fetchBookings
})
```

## API Client

Base URL should point to gateway:
```typescript
const apiClient = axios.create({
  baseURL: 'http://localhost:9090'  // API Gateway
})
```

## Service-Specific Rules

1. **TypeScript strict**: `bun run tsc --noEmit` must pass.
2. **Path aliases**: Use `@/` imports, no relative paths like `../../`.
3. **No CORS config**: CORS handled at backend gateway.
4. **Feature organization**: Code lives in features, not scattered.
5. **shadcn/ui**: Use for UI components, customize via Tailwind.
6. **Zod validation**: Validate forms with Zod schemas.
7. **React Hook Form**: Use for form state management.

## Environment Variables

```
VITE_API_URL=http://localhost:9090
```

Access: `import.meta.env.VITE_API_URL`

## Full Documentation

- [Frontend Architecture Docs](../../docs/frontend/architecture.md)
- [State Management Docs](../../docs/frontend/state-management.md)
- [Routing Docs](../../docs/frontend/routing.md)
- [Architecture Overview](../../AGENTS.md)

## Port

3000 (Vite dev server)
