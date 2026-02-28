import { Navigate } from 'react-router'
import { useAuthStore } from '@/features/auth/store'
import type { ReactNode } from 'react'

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
