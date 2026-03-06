# Frontend Architecture

React 18 + TypeScript application with Vite build tooling.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | React 18 |
| Language | TypeScript 5.9 (strict mode) |
| Build Tool | Vite 7 |
| Package Manager | Bun |
| Styling | Tailwind CSS 4 |
| UI Components | shadcn/ui + Radix |
| State | Zustand |
| Data Fetching | React Query 5 |
| Routing | React Router 7 |
| Validation | Zod |
| Forms | React Hook Form |
| Linting | Biome |

## Directory Structure

```
frontend/src/
├── components/        # shadcn/ui components
├── features/          # Domain-specific modules
│   ├── auth/         # Authentication
│   ├── customer/     # Customer views
│   └── specialist/   # Specialist views
├── hooks/            # Custom React hooks
├── lib/              # Utilities (utils.ts)
├── pages/            # Top-level page components
├── routes/           # Routing components
│   ├── ProtectedRoute.tsx
│   └── RootLayout.tsx
└── shared/           # Shared code
    ├── api/          # API client
    └── types/        # TypeScript types
```

## Feature-Based Organization

Features are self-contained modules:

```
features/customer/
├── Layout.tsx        # Feature layout with sidebar
├── Dashboard.tsx     # Dashboard view
├── Bookings.tsx      # Bookings list
├── BookingDetail.tsx # Booking detail
└── Profile.tsx       # Profile management
```

Each feature has its own components, hooks, and state as needed.

## TypeScript Configuration

Strict mode enabled with path aliases:

```json
{
  "compilerOptions": {
    "strict": true,
    "paths": {
      "@/*": ["./src/*"]
    }
  }
}
```

Path mapping in `vite.config.ts`:
```typescript
resolve: {
  alias: {
    '@': path.resolve(__dirname, './src')
  }
}
```

## Component Patterns

### shadcn/ui Components

Components installed via CLI:
```bash
npx shadcn add button card dialog
```

Located in `components/ui/`, styled with Tailwind, use `cn()` for class merging.

### Custom Components

Business components live in features or pages:

```typescript
// Use path alias for imports
import { Button } from '@/components/ui/button'
import { useAuthStore } from '@/features/auth/store'
```

## Styling

### Tailwind CSS 4

Using new CSS-first configuration:

```css
/* index.css */
@import "tailwindcss";

@theme {
  --color-primary: #0f766e;
  --color-secondary: #f59e0b;
}
```

### Class Variance Authority

Components use CVA for variant management:

```typescript
import { cva, type VariantProps } from 'class-variance-authority'

const buttonVariants = cva(
  'inline-flex items-center justify-center',
  {
    variants: {
      variant: {
        default: 'bg-primary text-white',
        outline: 'border border-gray-300'
      }
    }
  }
)
```

## API Client

Axios-based client with interceptors:

```typescript
// shared/api/client.ts
const apiClient = axios.create({
  baseURL: 'http://localhost:3001',
  headers: { 'Content-Type': 'application/json' }
})

// Request interceptor adds auth token
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor handles 401
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
```

## Build Commands

```bash
# Development
bun run dev

# Production build
bun run build

# Type checking
bun run type-check

# Linting
bun run lint
bun run lint:fix

# Formatting
bun run format
```

## Environment Variables

```
VITE_API_URL=http://localhost:9090
```

Access in code:
```typescript
const apiUrl = import.meta.env.VITE_API_URL
```

## Entry Points

- **Main**: `src/main.tsx` - React root render
- **App**: `src/App.tsx` - Router and providers
- **HTML**: `index.html` - Vite entry

## Testing Strategy

Unit tests with Vitest (to be added):
```bash
# Future: bun run test
```

E2E tests with Playwright (to be added).
