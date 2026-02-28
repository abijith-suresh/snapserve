export interface User {
  id: string
  email: string
  role: 'customer' | 'specialist' | 'admin'
}

export interface AuthState {
  user: User | null
  isAuthenticated: boolean
}
