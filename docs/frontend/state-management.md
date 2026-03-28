# State Management

Zustand for client state, React Query for server state.

## Overview

Two libraries handle different types of state:

- **Zustand**: Client state (auth, UI preferences)
- **React Query**: Server state (API data, caching)

This separation keeps the architecture clean and performant.

## Client State with Zustand

### When to Use

Use Zustand for data that:
- Doesn't come from the server
- Needs to persist across sessions
- Is local to the client

Examples:
- Authentication tokens and user info
- UI theme preferences
- Form drafts
- Modal open/close state

### Pattern

Store files located in feature folders:
```
features/
├── auth/
│   └── store.ts
├── customer/
│   └── store.ts
└── specialist/
    └── store.ts
```

### Best Practices

**Keep stores small and focused:**
- One store per feature
- Don't put server data in Zustand
- Use slices for complex state

**Persistence:**
- Use Zustand's persist middleware for localStorage
- Only persist necessary data (not sensitive tokens)
- Encrypt sensitive data if persisting

**Example Usage:**

See `frontend/src/features/auth/store.ts` for implementation.

## Server State with React Query

### When to Use

Use React Query for data that:
- Comes from API endpoints
- Needs caching
- Requires background updates
- Has loading/error states

Examples:
- User profiles
- Booking lists
- Specialist data
- Reviews

### Pattern

Query hooks located in feature folders:
```
features/
├── auth/
│   └── queries.ts
├── customer/
│   └── queries.ts
└── specialist/
    └── queries.ts
```

### Key Features

**Caching:**
- Automatic caching based on query keys
- Configurable stale time
- Background refetching when data is stale

**Loading States:**
- `isLoading` — First fetch
- `isFetching` — Any fetch (including background)
- No need for manual loading state management

**Error Handling:**
- Automatic retry on failure
- `error` object with details
- Configurable retry count

**Optimistic Updates:**
- Update UI before server confirms
- Rollback on error
- Great for mutations

### Best Practices

**Query Keys:**
- Use arrays: `['users', userId]`
- Include all dependencies
- Consistent naming across app

**Mutations:**
- Use for POST, PUT, DELETE
- Invalidate related queries on success
- Show success/error toasts

**Prefetching:**
- Prefetch on hover for better UX
- Use `queryClient.prefetchQuery`

## Choosing Between Zustand and React Query

| Use Case | Zustand | React Query |
|----------|---------|-------------|
| Auth tokens | ✅ | ❌ |
| User profile | ❌ | ✅ |
| UI theme | ✅ | ❌ |
| Booking list | ❌ | ✅ |
| Form draft | ✅ | ❌ |
| API cache | ❌ | ✅ |

**Rule of thumb:**
- If it comes from the server → React Query
- If it's client-only → Zustand

## Integration Pattern

Zustand and React Query work together:

1. **Login flow**:
   - React Query: Call login API
   - Zustand: Store tokens and user info on success

2. **Fetch user data**:
   - React Query: Fetch from `/api/v1/users/{id}`
   - Component: Display data from React Query

3. **Logout**:
   - Zustand: Clear auth state
   - React Query: Clear all queries (queryClient.clear())

## Configuration

**React Query Client** configured in `App.tsx`:
- Default stale time: 5 minutes
- Retry count: 3
- Refetch on window focus: false (configurable)

## Benefits of This Approach

**Separation of Concerns:**
- Client state separate from server state
- Clear responsibilities

**Performance:**
- React Query handles caching automatically
- No unnecessary re-renders
- Background updates keep data fresh

**Developer Experience:**
- Less boilerplate than Redux
- TypeScript support
- DevTools for debugging

## Common Pitfalls

**Don't:**
- Put API data in Zustand (use React Query)
- Mix server and client state in one store
- Forget to handle loading/error states
- Over-fetch (React Query deduplicates automatically)

**Do:**
- Keep stores small and focused
- Use consistent query keys
- Handle errors gracefully
- Leverage React Query's caching

## Links

- Zustand docs: https://docs.pmnd.rs/zustand
- React Query docs: https://tanstack.com/query/latest
- Example stores: `frontend/src/features/auth/store.ts`
- Example queries: `frontend/src/features/auth/queries.ts`
