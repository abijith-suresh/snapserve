import { Outlet } from 'react-router'
import { Link } from 'react-router'
import { useAuthStore } from '@/features/auth/store'

export default function RootLayout() {
  const { isAuthenticated, user, logout } = useAuthStore()

  return (
    <div className="min-h-screen">
      <nav className="border-b px-4 py-3">
        <div className="flex items-center justify-between max-w-6xl mx-auto">
          <Link to="/" className="font-bold text-xl">
            SnapServe
          </Link>
          <div className="flex items-center gap-4">
            {isAuthenticated ? (
              <>
                <span className="text-sm text-gray-600">{user?.email}</span>
                <button
                  onClick={logout}
                  className="text-sm px-3 py-1 bg-gray-100 rounded hover:bg-gray-200"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="text-sm">
                  Login
                </Link>
                <Link to="/signup" className="text-sm px-3 py-1 bg-blue-600 text-white rounded">
                  Sign Up
                </Link>
              </>
            )}
          </div>
        </div>
      </nav>
      <main>
        <Outlet />
      </main>
    </div>
  )
}
