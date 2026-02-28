import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { User } from '@/shared/types'

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
          user: { id: '1', email, role: role as any },
          isAuthenticated: true,
        })
      },
      logout: () => {
        set({ user: null, isAuthenticated: false })
      },
    }),
    { name: 'auth' }
  )
)
