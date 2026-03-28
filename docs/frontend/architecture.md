# Frontend Architecture

React SPA structure and technology choices.

## Tech Stack

- **Framework**: React 19
- **Language**: TypeScript 5.9 (strict mode)
- **Build Tool**: Vite
- **Package Manager**: Bun
- **Styling**: Tailwind CSS v4
- **Components**: shadcn/ui (built on Radix UI)
- **State Management**: Zustand (client), React Query (server)
- **Forms**: React Hook Form + Zod
- **Routing**: React Router 7
- **HTTP Client**: Axios
- **Linting**: Biome

## Project Structure

Feature-based organization for scalability:

```
src/
├── components/ui/     # shadcn/ui components (reusable)
├── features/          # Feature modules
│   ├── auth/         # Authentication
│   ├── customer/     # Customer portal
│   └── specialist/   # Specialist portal
├── pages/            # Public pages
├── routes/           # Route definitions and layouts
├── shared/           # Shared resources
│   ├── api/         # Axios client config
│   └── types/       # TypeScript types
├── hooks/            # Custom React hooks
└── lib/              # Utility functions
```

## Feature Modules

Each feature is self-contained:

**auth/**
- Store: `store.ts` (Zustand)
- API: `api.ts` (React Query hooks)
- Types: `types.ts`

**customer/**
- Components: Dashboard, Bookings, Profile pages
- Store: Customer-specific state
- API: Customer-related queries

**specialist/**
- Components: Dashboard, Appointments, Profile pages
- Store: Specialist-specific state
- API: Specialist-related queries

## Component Guidelines

### shadcn/ui Components

Use built-in components from `components/ui/`:
- Button, Card, Input, Label, etc.
- Styled with Tailwind CSS
- Accessible (Radix UI primitives)
- Customizable via className

### Custom Components

- Keep components small and focused
- Use TypeScript interfaces for props
- Prefer composition over inheritance
- Co-locate related components in feature folders

## State Management Strategy

### Client State (Zustand)

Use for:
- Authentication state (user, tokens)
- UI state (modals, theme)
- Form state (drafts)

Stored in feature folders: `features/<feature>/store.ts`

### Server State (React Query)

Use for:
- API data fetching
- Caching server responses
- Background updates
- Optimistic updates

Stored in feature folders: `features/<feature>/queries.ts`

### When to use which?

**Zustand**: Data that doesn't come from server (auth tokens, UI preferences)
**React Query**: Data that comes from API (users, bookings, profiles)

## TypeScript Configuration

Strict mode enabled:
- `strict: true`
- `noImplicitAny: true`
- `strictNullChecks: true`

All code must pass: `bun run typecheck`

## API Integration

### Axios Client

Configured in `shared/api/client.ts`:
- Base URL: `http://localhost:9090` (gateway)
- Request interceptor: Adds Bearer token
- Response interceptor: Handles 401 (logout)

### API Calls Pattern

Use React Query hooks:
- `useQuery` for reading data
- `useMutation` for writing data
- Define in feature folders
- Reuse across components

## Styling

### Tailwind CSS v4

CSS-first configuration in `index.css`:
- Theme colors defined via CSS variables
- shadcn/ui uses Tailwind classes
- Responsive design with Tailwind breakpoints

### Styling Approach

- Use Tailwind utility classes
- Avoid custom CSS when possible
- Use `cn()` utility for conditional classes
- Follow shadcn/ui component patterns

## Routing

React Router 7 with data API:
- Route definitions in `routes/`
- Layout routes for shared UI
- Protected routes with role checks
- Nested routes for features

See [routing.md](./routing.md) for details.

## Code Quality

### Biome Configuration

- Linting and formatting in one tool
- Fast (Rust-based)
- Replaces ESLint + Prettier

Commands:
```bash
bun run lint       # Check issues
bun run lint:fix   # Auto-fix
bun run format     # Format code
```

### Pre-commit Hooks

Husky + lint-staged:
- Runs Biome on staged files
- Enforces conventional commits

## Build and Deploy

### Development

```bash
bun run dev    # Start dev server (port 3000)
```

### Production Build

```bash
bun run build  # Create production build
```

### Type Checking

```bash
bun run typecheck  # Verify TypeScript (strict mode)
```

## Environment Variables

Vite uses `.env` files:
- `VITE_API_URL` — API gateway URL

Prefix all env vars with `VITE_` to expose to client.

## Links

- Source: `frontend/src/`
- Components: `frontend/src/components/ui/`
- State management: [state-management.md](./state-management.md)
- Routing: [routing.md](./routing.md)
- Code style: [../standards/code-style.md](../standards/code-style.md)
