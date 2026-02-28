import { Outlet, Link } from 'react-router'
import { useAuthStore } from '@/features/auth/store'
import { Search, LayoutDashboard } from 'lucide-react'

export default function RootLayout() {
  const { isAuthenticated, logout } = useAuthStore()

  return (
    <div className="min-h-screen">
      <nav className="border-b px-4 py-3">
        <div className="flex items-center justify-between max-w-6xl mx-auto">
          <Link to="/" className="font-bold text-xl">
            SnapServe
          </Link>

          <div className="flex items-center gap-6">
            {/* Public navigation */}
            <Link
              to="/specialists"
              className="flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900"
            >
              <Search className="h-4 w-4" />
              Browse Specialists
            </Link>

            <div className="h-4 w-px bg-gray-300" />

            {isAuthenticated ? (
              <>
                <Link
                  to="/dashboard"
                  className="flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900"
                >
                  <LayoutDashboard className="h-4 w-4" />
                  Dashboard
                </Link>
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
                <Link
                  to="/signup"
                  className="text-sm px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
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
