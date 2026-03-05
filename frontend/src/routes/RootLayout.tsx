import { LayoutDashboard, Search } from 'lucide-react'
import { Link, Outlet } from 'react-router'
import { useAuthStore } from '@/features/auth/store'

export default function RootLayout() {
  const { isAuthenticated, logout } = useAuthStore()

  return (
    <div className="min-h-screen" style={{ fontFamily: "'Manrope', sans-serif" }}>
      <nav className="border-b px-4 py-3">
        <div className="flex items-center justify-between max-w-6xl mx-auto">
          <Link to="/" className="font-bold text-xl">
            <span className="text-slate-900">Snap</span>
            <span className="text-emerald-600">Serve</span>
          </Link>

          <div className="flex items-center gap-6">
            <Link
              to="/specialists"
              className="flex items-center gap-2 text-sm text-slate-600 hover:text-slate-900"
            >
              <Search className="h-4 w-4" />
              Browse Specialists
            </Link>

            <div className="h-4 w-px bg-slate-300" />

            {isAuthenticated ? (
              <>
                <Link
                  to="/dashboard"
                  className="flex items-center gap-2 text-sm text-slate-600 hover:text-slate-900"
                >
                  <LayoutDashboard className="h-4 w-4" />
                  Dashboard
                </Link>
                <button
                  onClick={logout}
                  className="text-sm px-3 py-1 bg-slate-100 rounded hover:bg-slate-200"
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
                  className="text-sm px-3 py-1 bg-emerald-600 text-white rounded hover:bg-emerald-700"
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
