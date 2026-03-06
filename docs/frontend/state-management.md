# State Management

Zustand for global state, React Query for server state.

## Philosophy

- **Zustand**: Client-only state (auth, UI preferences)
- **React Query**: Server state (caching, background updates)
- **Local state**: useState for component-level state

## Zustand Stores

### Auth Store

Global authentication state with persistence:

```typescript
// features/auth/store.ts
import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface AuthStore {
  user: User | null
  isAuthenticated: boolean
  login: (email: string, role: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,
      login: (email, role) => {
        set({
          user: { id: '1', email, role: role as 'customer' | 'specialist' },
          isAuthenticated: true
        })
      },
      logout: () => {
        set({ user: null, isAuthenticated: false })
      }
    }),
    { name: 'auth' } // localStorage key
  )
)
```

Usage:
```typescript
import { useAuthStore } from '@/features/auth/store'

function Dashboard() {
  const { user, isAuthenticated, logout } = useAuthStore()
  
  if (!isAuthenticated) return <Navigate to="/login" />
  
  return <div>Welcome {user?.email}</div>
}
```

### Store Best Practices

1. **One store per domain**: auth, cart, preferences
2. **Keep stores small**: Under 10 actions ideally
3. **Use TypeScript**: Full type inference
4. **Persist selectively**: Only persist what's needed
5. **No nested stores**: Flat structure

## React Query

Server state management with caching:

```typescript
// App.tsx
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      retry: 2
    }
  }
})

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  )
}
```

### Query Hooks Pattern

```typescript
// features/bookings/hooks/useBookings.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'

const BOOKINGS_KEY = 'bookings'

export function useBookings() {
  return useQuery({
    queryKey: [BOOKINGS_KEY],
    queryFn: async () => {
      const response = await apiClient.get('/api/v1/bookings')
      return response.data
    }
  })
}

export function useCreateBooking() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: async (booking: BookingDto) => {
      const response = await apiClient.post('/api/v1/bookings', booking)
      return response.data
    },
    onSuccess: () => {
      // Invalidate and refetch
      queryClient.invalidateQueries({ queryKey: [BOOKINGS_KEY] })
    }
  })
}
```

### Usage in Components

```typescript
function BookingList() {
  const { data: bookings, isLoading, error } = useBookings()
  const createBooking = useCreateBooking()
  
  if (isLoading) return <Spinner />
  if (error) return <Error message={error.message} />
  
  return (
    <>
      {bookings?.map(booking => (
        <BookingCard key={booking.id} booking={booking} />
      ))}
      <Button onClick={() => createBooking.mutate(newBooking)}>
        Create Booking
      </Button>
    </>
  )
}
```

## State Separation Guidelines

| State Type | Where | Example |
|------------|-------|---------|
| Global Client | Zustand | Auth, theme |
| Global Server | React Query | Bookings, users |
| Feature Local | useState | Form inputs, modals |
| Shared Feature | Context | Feature-specific data |

## Common Patterns

### Combining Zustand and React Query

```typescript
function useAuthUser() {
  // Zustand for auth state
  const { isAuthenticated } = useAuthStore()
  
  // React Query for user data
  const { data: user } = useQuery({
    queryKey: ['user'],
    queryFn: fetchUser,
    enabled: isAuthenticated // Only fetch if logged in
  })
  
  return { isAuthenticated, user }
}
```

### Optimistic Updates

```typescript
const updateBooking = useMutation({
  mutationFn: updateBookingApi,
  onMutate: async (newBooking) => {
    await queryClient.cancelQueries({ queryKey: ['bookings'] })
    const previous = queryClient.getQueryData(['bookings'])
    queryClient.setQueryData(['bookings'], (old) => 
      old?.map(b => b.id === newBooking.id ? newBooking : b)
    )
    return { previous }
  },
  onError: (err, newBooking, context) => {
    queryClient.setQueryData(['bookings'], context.previous)
  },
  onSettled: () => {
    queryClient.invalidateQueries({ queryKey: ['bookings'] })
  }
})
```

## DevTools

Zustand supports Redux DevTools:

```typescript
import { devtools } from 'zustand/middleware'

const useStore = create(
  devtools(
    persist(...),
    { name: 'AuthStore' }
  )
)
```

React Query has built-in DevTools (add to dev dependencies):
```typescript
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
```
