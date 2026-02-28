import { Outlet, Link, useLocation } from 'react-router'
import { useAuthStore } from '@/features/auth/store'
import { LayoutDashboard, Calendar, User } from 'lucide-react'

const navigation = [
  { name: 'Dashboard', href: '/specialist/dashboard', icon: LayoutDashboard },
  { name: 'Appointments', href: '/specialist/appointments', icon: Calendar },
  { name: 'Profile', href: '/specialist/profile', icon: User },
]

export default function SpecialistLayout() {
  const { logout, user } = useAuthStore()
  const location = useLocation()

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <div className="w-64 bg-white border-r flex flex-col">
        <div className="p-4 border-b">
          <Link to="/specialist/dashboard" className="font-bold text-xl">
            SnapServe
          </Link>
          <p className="text-sm text-gray-500 mt-1">Specialist Portal</p>
        </div>

        <nav className="flex-1 p-4 space-y-1">
          {navigation.map((item) => {
            const Icon = item.icon
            const isActive = location.pathname === item.href
            return (
              <Link
                key={item.name}
                to={item.href}
                className={`flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                  isActive ? 'bg-green-50 text-green-600' : 'text-gray-700 hover:bg-gray-100'
                }`}
              >
                <Icon className="h-5 w-5" />
                {item.name}
              </Link>
            )
          })}
        </nav>

        <div className="p-4 border-t">
          <div className="flex items-center gap-3 mb-3">
            <div className="h-8 w-8 rounded-full bg-green-600 text-white flex items-center justify-center text-sm font-medium">
              {user?.email?.charAt(0).toUpperCase()}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-gray-900 truncate">{user?.email}</p>
              <p className="text-xs text-gray-500">Specialist</p>
            </div>
          </div>
          <button
            onClick={logout}
            className="w-full px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
          >
            Logout
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 overflow-auto">
        <main className="p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
