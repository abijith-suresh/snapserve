import { Link } from 'react-router'

export default function HomePage() {
  return (
    <div className="max-w-6xl mx-auto px-4 py-20">
      <div className="text-center">
        <h1 className="text-5xl font-bold mb-6">Welcome to SnapServe</h1>
        <p className="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
          Book appointments with specialists quickly and easily.
        </p>
        <div className="flex gap-4 justify-center">
          <Link
            to="/signup"
            className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Get Started
          </Link>
          <Link to="/login" className="px-6 py-3 border rounded-lg hover:bg-gray-50">
            Sign In
          </Link>
        </div>
      </div>
    </div>
  )
}
