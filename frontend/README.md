# Frontend

React 19 + TypeScript + Tailwind CSS v4 + shadcn/ui + React Router v7

## Development

```bash
# Install dependencies
bun install

# Run dev server
bun dev

# Build for production
bun run build
```

Dev server runs at: http://localhost:5173/

## Structure

```
src/
├── components/ui/     # shadcn/ui components
├── features/
│   ├── auth/         # Auth store
│   ├── customer/     # Customer features
│   └── specialist/   # Specialist features
├── lib/
│   └── utils.ts      # Utility functions
├── pages/            # Page components
├── routes/           # Route components
└── shared/
    └── types/        # TypeScript types
```

## Tech Stack

- **React 19** - UI library
- **TypeScript** - Type safety
- **Tailwind CSS v4** - Styling
- **shadcn/ui** - UI components
- **React Router v7** - Routing
- **Zustand** - State management
- **TanStack Query** - Server state
- **React Hook Form** - Form handling
- **Zod** - Schema validation

## Current Features

- Landing page
- Login page (mock auth)
- Signup page
- 404 page
- Protected routes for customer/specialist
- Basic dashboards
