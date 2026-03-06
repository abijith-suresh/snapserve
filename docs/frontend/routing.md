# Routing

React Router 7 configuration for role-based navigation.

## Overview

React Router 7 with data API pattern:
- Declarative route definitions
- Nested routes for layouts
- Protected routes with authentication
- Role-based access control

## Route Structure

```
/                    # Home (public)
/login               # Login (public)
/signup              # Signup (public)
/specialists         # Browse specialists (public)
/specialists/:id     # Specialist profile (public)
/customer/*          # Customer portal (protected)
/specialist/*        # Specialist portal (protected)
/dashboard           # Redirect based on role
/bookings            # Redirect based on role
/profile             # Redirect based on role
```

## Route Types

### Public Routes

Accessible without authentication:
- `/` — Home page
- `/login` — Login form
- `/signup` — Registration form
- `/specialists` — Browse specialists
- `/specialists/:id` — Specialist profile

### Protected Routes

Require authentication:

**Customer Portal** (`/customer/*`):
- `/customer/dashboard` — Customer dashboard
- `/customer/bookings` — My bookings
- `/customer/bookings/:id` — Booking details
- `/customer/profile` — Edit profile

**Specialist Portal** (`/specialist/*`):
- `/specialist/dashboard` — Specialist dashboard
- `/specialist/appointments` — My appointments
- `/specialist/appointments/:id` — Appointment details
- `/specialist/profile` — Edit profile

### Smart Redirects

Routes that redirect based on role:
- `/dashboard` → `/customer/dashboard` or `/specialist/dashboard`
- `/bookings` → `/customer/bookings` or `/specialist/appointments`
- `/profile` → `/customer/profile` or `/specialist/profile`

## Route Components

### RootLayout

Main layout for all routes:
- Navigation header
- Footer
- Outlet for page content

### CustomerLayout

Layout for customer portal:
- Sidebar navigation
- Customer-specific menu
- Protected (requires CUSTOMER role)

### SpecialistLayout

Layout for specialist portal:
- Sidebar navigation
- Specialist-specific menu
- Protected (requires SPECIALIST role)

### ProtectedRoute

Wrapper for protected routes:
- Checks authentication
- Validates role
- Redirects to login if not authenticated

## Authentication Flow

1. User logs in via `/login`
2. Auth store updates with tokens and user info
3. Router redirects to appropriate dashboard
4. Protected routes check auth on each navigation

## Role-Based Access

Role extracted from JWT token:
- `CUSTOMER` — Access to customer portal
- `SPECIALIST` — Access to specialist portal
- `ADMIN` — Future admin dashboard

If user tries to access wrong role's routes:
- Redirect to their own dashboard
- Or show 403 Forbidden page

## Route Configuration

Routes defined in `App.tsx` using `createBrowserRouter`.

See `frontend/src/App.tsx` for implementation.

## Lazy Loading

Route components can be lazy loaded for better performance:
- Use `React.lazy()` for page components
- Show loading spinner while loading
- Split by route for optimal bundle size

## Navigation

### Programmatic Navigation

Use `useNavigate` hook from React Router.

See React Router docs for examples.

### Link Component

Use `Link` component for declarative navigation:

```typescript
import { Link } from 'react-router-dom';

<Link to="/specialists">Browse Specialists</Link>
```

### Navigation with State

Pass state when navigating:

```typescript
navigate('/booking/123', { state: { from: 'search' } });
```

## URL Parameters

Access route parameters:

```typescript
import { useParams } from 'react-router-dom';

const { id } = useParams(); // /specialists/:id
```

## Query Parameters

Access and set query params:

```typescript
import { useSearchParams } from 'react-router-dom';

const [searchParams, setSearchParams] = useSearchParams();
const category = searchParams.get('category');
```

## Best Practices

**Route Organization:**
- Keep route definitions in one place (App.tsx)
- Use nested routes for layouts
- Group related routes

**Protected Routes:**
- Always check auth in ProtectedRoute wrapper
- Validate role, not just authentication
- Redirect to login with return URL

**Performance:**
- Lazy load page components
- Preload routes on hover (optional)
- Keep layout components lightweight

## 404 Handling

Catch-all route for undefined paths:

```typescript
{ path: '*', element: <NotFoundPage /> }
```

## Links

- Route definitions: `frontend/src/App.tsx`
- Layout components: `frontend/src/routes/`
- Protected route logic: `frontend/src/routes/ProtectedRoute.tsx`
- React Router docs: https://reactrouter.com/
