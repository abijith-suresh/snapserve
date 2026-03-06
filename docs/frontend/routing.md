# Routing

React Router v7 with role-based route protection.

## Router Configuration

```typescript
// App.tsx
import { createBrowserRouter, Navigate, RouterProvider } from 'react-router'
import { useAuthStore } from './features/auth/store'
import { ProtectedRoute } from './routes/ProtectedRoute'
import RootLayout from './routes/RootLayout'

const router = createBrowserRouter([
  // Landing page - standalone
  { path: '/', element: <HomePage /> },
  
  // Public routes with layout
  {
    element: <RootLayout />,
    children: [
      { path: 'login', element: <LoginPage /> },
      { path: 'signup', element: <SignupPage /> },
      { path: 'specialists', element: <BrowseSpecialists /> },
      { path: 'specialists/:id', element: <SpecialistProfile /> },
      { path: '*', element: <NotFoundPage /> }
    ]
  },
  
  // Customer routes (protected)
  {
    path: '/customer',
    element: (
      <ProtectedRoute allowedRoles={['customer']}>
        <CustomerLayout />
      </ProtectedRoute>
    ),
    children: [
      { path: 'dashboard', element: <CustomerDashboard /> },
      { path: 'specialists', element: <BrowseSpecialists /> },
      { path: 'bookings', element: <CustomerBookings /> },
      { path: 'bookings/:id', element: <CustomerBookingDetail /> },
      { path: 'profile', element: <CustomerProfile /> },
      { index: true, element: <Navigate to="dashboard" replace /> }
    ]
  },
  
  // Specialist routes (protected)
  {
    path: '/specialist',
    element: (
      <ProtectedRoute allowedRoles={['specialist']}>
        <SpecialistLayout />
      </ProtectedRoute>
    ),
    children: [
      { path: 'dashboard', element: <SpecialistDashboard /> },
      { path: 'appointments', element: <SpecialistAppointments /> },
      { path: 'appointments/:id', element: <SpecialistAppointmentDetail /> },
      { path: 'profile', element: <SpecialistProfile /> },
      { index: true, element: <Navigate to="dashboard" replace /> }
    ]
  }
])
```

## Protected Route Component

```typescript
// routes/ProtectedRoute.tsx
import type { ReactNode } from 'react'
import { Navigate } from 'react-router'
import { useAuthStore } from '@/features/auth/store'

interface ProtectedRouteProps {
  children: ReactNode
  allowedRoles: string[]
}

export function ProtectedRoute({ children, allowedRoles }: ProtectedRouteProps) {
  const { isAuthenticated, user } = useAuthStore()
  
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }
  
  if (!allowedRoles.includes(user?.role || '')) {
    return <Navigate to="/" replace />
  }
  
  return <>{children}</>
}
```

## Root Layout

```typescript
// routes/RootLayout.tsx
import { Outlet } from 'react-router'
import { Navbar } from '@/components/Navbar'
import { Footer } from '@/components/Footer'

export default function RootLayout() {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}
```

## Feature Layouts

Customer and specialist layouts include sidebar navigation:

```typescript
// features/customer/Layout.tsx
import { Outlet, NavLink } from 'react-router'

export default function CustomerLayout() {
  return (
    <div className="flex min-h-screen">
      <aside className="w-64 bg-gray-50 border-r">
        <nav className="p-4 space-y-2">
          <NavLink to="/customer/dashboard" className={...}>
            Dashboard
          </NavLink>
          <NavLink to="/customer/bookings" className={...}>
            My Bookings
          </NavLink>
          <NavLink to="/customer/profile" className={...}>
            Profile
          </NavLink>
        </nav>
      </aside>
      <main className="flex-1 p-6">
        <Outlet />
      </main>
    </div>
  )
}
```

## Redirect Components

Smart redirects based on user role:

```typescript
// Dashboard redirect
function DashboardRedirect() {
  const { user } = useAuthStore()
  if (!user) return <Navigate to="/login" replace />
  return <Navigate to={`/${user.role}/dashboard`} replace />
}

// Bookings redirect
function BookingsRedirect() {
  const { user } = useAuthStore()
  if (!user) return <Navigate to="/login" replace />
  if (user.role === 'customer') return <Navigate to="/customer/bookings" replace />
  if (user.role === 'specialist') return <Navigate to="/specialist/appointments" replace />
  return <Navigate to="/" replace />
}

// Profile redirect
function ProfileRedirect() {
  const { user } = useAuthStore()
  if (!user) return <Navigate to="/login" replace />
  return <Navigate to={`/${user.role}/profile`} replace />
}
```

## Route Structure

| Path | Access | Description |
|------|--------|-------------|
| `/` | Public | Home page |
| `/login` | Public | Login form |
| `/signup` | Public | Registration |
| `/specialists` | Public | Browse specialists |
| `/specialists/:id` | Public | Specialist profile |
| `/dashboard` | Redirect | → /customer/dashboard or /specialist/dashboard |
| `/bookings` | Redirect | → /customer/bookings or /specialist/appointments |
| `/profile` | Redirect | → /customer/profile or /specialist/profile |
| `/customer/*` | Customer only | Customer dashboard area |
| `/specialist/*` | Specialist only | Specialist dashboard area |
| `*` | All | 404 Not Found |

## Navigation Patterns

### Programmatic Navigation

```typescript
import { useNavigate } from 'react-router'

function LoginForm() {
  const navigate = useNavigate()
  
  const handleLogin = async (credentials) => {
    await login(credentials)
    navigate('/dashboard')
  }
}
```

### Link with State

```typescript
<Link to="/bookings" state={{ from: location }}>
  View Bookings
</Link>

// Access in target component
import { useLocation } from 'react-router'
const location = useLocation()
const from = location.state?.from
```

### URL Parameters

```typescript
// Route definition
{ path: 'specialists/:id', element: <SpecialistProfile /> }

// Access parameter
import { useParams } from 'react-router'

function SpecialistProfile() {
  const { id } = useParams()
  // Fetch specialist with id
}
```

## Lazy Loading

For future optimization:

```typescript
const CustomerDashboard = lazy(() => 
  import('./features/customer/Dashboard')
)

{
  path: 'dashboard',
  element: (
    <Suspense fallback={<Spinner />}>
      <CustomerDashboard />
    </Suspense>
  )
}
```
