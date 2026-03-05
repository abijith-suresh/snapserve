import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { createBrowserRouter, Navigate, RouterProvider } from 'react-router'
import { useAuthStore } from './features/auth/store'
import CustomerBookingDetail from './features/customer/BookingDetail'
import CustomerBookings from './features/customer/Bookings'
import CustomerDashboard from './features/customer/Dashboard'
import CustomerLayout from './features/customer/Layout'
import CustomerProfile from './features/customer/Profile'
import SpecialistAppointmentDetail from './features/specialist/AppointmentDetail'
import SpecialistAppointments from './features/specialist/Appointments'
import SpecialistDashboard from './features/specialist/Dashboard'
import SpecialistLayout from './features/specialist/Layout'
import SpecialistProfileManage from './features/specialist/Profile'
import BrowseSpecialists from './pages/BrowseSpecialists'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import NotFoundPage from './pages/NotFoundPage'
import SignupPage from './pages/SignupPage'
import SpecialistProfile from './pages/SpecialistProfile'
import { ProtectedRoute } from './routes/ProtectedRoute'
import RootLayout from './routes/RootLayout'
import './index.css'

const queryClient = new QueryClient()

// Redirect component for /dashboard
function DashboardRedirect() {
  const { user } = useAuthStore()
  if (!user) return <Navigate to="/login" replace />
  return <Navigate to={`/${user.role}/dashboard`} replace />
}

// Redirect component for /bookings
function BookingsRedirect() {
  const { user } = useAuthStore()
  if (!user) return <Navigate to="/login" replace />
  if (user.role === 'customer') return <Navigate to="/customer/bookings" replace />
  if (user.role === 'specialist') return <Navigate to="/specialist/appointments" replace />
  return <Navigate to="/" replace />
}

// Redirect component for /profile
function ProfileRedirect() {
  const { user } = useAuthStore()
  if (!user) return <Navigate to="/login" replace />
  return <Navigate to={`/${user.role}/profile`} replace />
}

const router = createBrowserRouter([
  // Landing page — standalone, own navbar/footer
  { path: '/', element: <HomePage /> },
  // Public pages under RootLayout
  {
    element: <RootLayout />,
    children: [
      { path: 'login', element: <LoginPage /> },
      { path: 'signup', element: <SignupPage /> },
      { path: 'specialists', element: <BrowseSpecialists /> },
      { path: 'specialists/:id', element: <SpecialistProfile /> },
      { path: 'dashboard', element: <DashboardRedirect /> },
      { path: 'bookings', element: <BookingsRedirect /> },
      { path: 'profile', element: <ProfileRedirect /> },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
  // Customer Routes
  {
    path: '/customer',
    element: (
      <ProtectedRoute allowedRoles={['customer']}>
        <CustomerLayout />
      </ProtectedRoute>
    ),
    children: [
      { path: 'dashboard', element: <CustomerDashboard /> },
      { path: 'specialists', element: <BrowseSpecialists /> },
      { path: 'specialists/:id', element: <SpecialistProfile /> },
      { path: 'bookings', element: <CustomerBookings /> },
      { path: 'bookings/:id', element: <CustomerBookingDetail /> },
      { path: 'profile', element: <CustomerProfile /> },
      { index: true, element: <Navigate to="dashboard" replace /> },
    ],
  },
  // Specialist Routes
  {
    path: '/specialist',
    element: (
      <ProtectedRoute allowedRoles={['specialist']}>
        <SpecialistLayout />
      </ProtectedRoute>
    ),
    children: [
      { path: 'dashboard', element: <SpecialistDashboard /> },
      { path: 'appointments', element: <SpecialistAppointments /> },
      { path: 'appointments/:id', element: <SpecialistAppointmentDetail /> },
      { path: 'profile', element: <SpecialistProfileManage /> },
      { index: true, element: <Navigate to="dashboard" replace /> },
    ],
  },
])

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  )
}

export default App
