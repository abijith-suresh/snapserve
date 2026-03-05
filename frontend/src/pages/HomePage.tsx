import {
  CheckCircle,
  Hammer,
  Leaf,
  Menu,
  Paintbrush,
  Sparkles,
  Star,
  Truck,
  Wind,
  Wrench,
  X,
  Zap,
} from 'lucide-react'
import type { ComponentType } from 'react'
import { useState } from 'react'
import { Link, Navigate } from 'react-router'
import { useAuthStore } from '@/features/auth/store'

interface ServiceCategory {
  label: string
  icon: ComponentType<{ className?: string }>
  iconBg: string
}
interface Stat {
  value: string
  label: string
}
interface Step {
  number: string
  title: string
  description: string
}
interface Specialist {
  name: string
  title: string
  rating: number
  reviews: number
  initials: string
}
interface Testimonial {
  quote: string
  author: string
  role: string
}

const services: ServiceCategory[] = [
  { label: 'Plumbing', icon: Wrench, iconBg: 'bg-blue-50 text-blue-500' },
  { label: 'Electrical', icon: Zap, iconBg: 'bg-yellow-50 text-yellow-500' },
  { label: 'Carpentry', icon: Hammer, iconBg: 'bg-orange-50 text-orange-500' },
  { label: 'Cleaning', icon: Sparkles, iconBg: 'bg-purple-50 text-purple-500' },
  { label: 'Painting', icon: Paintbrush, iconBg: 'bg-pink-50 text-pink-500' },
  { label: 'HVAC', icon: Wind, iconBg: 'bg-cyan-50 text-cyan-500' },
  { label: 'Landscaping', icon: Leaf, iconBg: 'bg-green-50 text-green-500' },
  { label: 'Moving', icon: Truck, iconBg: 'bg-red-50 text-red-500' },
]

const stats: Stat[] = [
  { value: '12,000+', label: 'Bookings Completed' },
  { value: '850+', label: 'Verified Specialists' },
  { value: '4.8/5', label: 'Average Rating' },
  { value: '50+', label: 'Cities Served' },
]

const steps: Step[] = [
  {
    number: '01',
    title: 'Search',
    description: 'Find the right specialist for your home service needs in seconds.',
  },
  {
    number: '02',
    title: 'Book',
    description: 'Schedule an appointment at a time that works perfectly for you.',
  },
  {
    number: '03',
    title: 'Done',
    description: 'Get the job done right by a vetted, trusted professional.',
  },
]

const specialists: Specialist[] = [
  { name: 'Alex Johnson', title: 'Master Electrician', rating: 4.9, reviews: 128, initials: 'AJ' },
  { name: 'Sarah Chen', title: 'Expert Plumber', rating: 4.8, reviews: 95, initials: 'SC' },
  { name: 'Marcus Williams', title: 'Carpenter', rating: 4.7, reviews: 72, initials: 'MW' },
]

const testimonials: Testimonial[] = [
  {
    quote:
      'SnapServe made it so easy to find a reliable plumber. The booking process was completely seamless.',
    author: 'Emily T.',
    role: 'Homeowner',
  },
  {
    quote:
      'As a business owner, I rely on SnapServe for all our facility maintenance. Outstanding service every time.',
    author: 'James R.',
    role: 'Business Owner',
  },
  {
    quote:
      'I was amazed by how quickly a specialist arrived. Highly recommend SnapServe to everyone.',
    author: 'Priya K.',
    role: 'Homeowner',
  },
]

function Navbar() {
  const [open, setOpen] = useState<boolean>(false)
  return (
    <nav
      className="bg-white/80 backdrop-blur-md sticky top-0 z-50 border-b border-slate-100"
      style={{ fontFamily: "'Manrope', sans-serif" }}
    >
      <div className="max-w-7xl mx-auto px-6 flex items-center justify-between h-16">
        <Link to="/" className="text-xl font-extrabold text-slate-900">
          Snap<span className="text-emerald-600">Serve</span>
        </Link>
        <div className="hidden md:flex items-center gap-8">
          <Link
            to="/specialists"
            className="text-slate-500 text-sm font-medium hover:text-slate-900 transition-colors"
          >
            Browse Services
          </Link>
          <Link
            to="/login"
            className="text-slate-500 text-sm font-medium hover:text-slate-900 transition-colors"
          >
            Login
          </Link>
          <Link
            to="/signup"
            className="bg-emerald-600 text-white text-sm font-semibold rounded-full px-5 py-2 hover:bg-emerald-700 transition-colors"
          >
            Get Started
          </Link>
        </div>
        <button className="md:hidden" onClick={() => setOpen(!open)} aria-label="Toggle menu">
          {open ? (
            <X className="w-5 h-5 text-slate-900" />
          ) : (
            <Menu className="w-5 h-5 text-slate-900" />
          )}
        </button>
      </div>
      {open && (
        <div className="md:hidden border-t border-slate-100 bg-white px-6 py-4 flex flex-col gap-4">
          <Link to="/specialists" className="text-slate-500 text-sm font-medium">
            Browse Services
          </Link>
          <Link to="/login" className="text-slate-500 text-sm font-medium">
            Login
          </Link>
          <Link to="/signup" className="text-sm font-semibold text-emerald-600">
            Get Started
          </Link>
        </div>
      )}
    </nav>
  )
}

function HeroSection() {
  return (
    <section className="py-16 md:py-24 min-h-[85vh] flex items-center">
      <div className="max-w-7xl mx-auto px-6 grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
        <div>
          <span className="inline-flex items-center gap-2 bg-emerald-50 text-emerald-600 text-xs font-semibold rounded-full px-3 py-1 mb-6">
            <CheckCircle className="w-3.5 h-3.5" />
            Trusted by 12,000+ homeowners
          </span>
          <h1
            className="text-4xl md:text-5xl font-extrabold text-slate-900 leading-tight"
            style={{ fontFamily: "'Manrope', sans-serif" }}
          >
            Home services you can actually trust.
          </h1>
          <p
            className="text-lg text-slate-500 mt-4 max-w-md"
            style={{ fontFamily: "'Manrope', sans-serif" }}
          >
            Connect with verified professionals for every home need. Book instantly, pay
            transparently.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link
              to="/signup"
              className="bg-emerald-600 text-white rounded-full px-8 py-3 text-sm font-semibold hover:bg-emerald-700 transition-colors"
            >
              Get Started
            </Link>
            <Link
              to="/specialists"
              className="border border-slate-200 text-slate-700 rounded-full px-8 py-3 text-sm font-medium hover:bg-slate-50 transition-colors"
            >
              Browse Services
            </Link>
          </div>
          <div className="mt-8 flex items-center gap-6">
            {['Verified Pros', 'Instant Booking', 'Satisfaction Guaranteed'].map((item) => (
              <span key={item} className="flex items-center gap-1.5 text-slate-500 text-sm">
                <CheckCircle className="w-4 h-4 text-emerald-600" />
                {item}
              </span>
            ))}
          </div>
        </div>
        <div className="bg-gradient-to-br from-emerald-50 to-teal-50 rounded-3xl p-6">
          <div className="grid grid-cols-3 gap-3">
            {services.slice(0, 6).map((s) => (
              <div
                key={s.label}
                className="bg-white rounded-2xl shadow-sm p-4 flex flex-col items-center gap-2 border border-slate-100"
              >
                <div
                  className={`w-10 h-10 rounded-xl ${s.iconBg} flex items-center justify-center`}
                >
                  <s.icon className="w-5 h-5" />
                </div>
                <span className="text-slate-700 text-xs font-medium">{s.label}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  )
}

function ServicesSection() {
  return (
    <section className="py-20 bg-slate-50">
      <div className="max-w-7xl mx-auto px-6">
        <h2
          className="text-2xl font-bold text-slate-900"
          style={{ fontFamily: "'Manrope', sans-serif" }}
        >
          Our Services
        </h2>
        <p className="text-slate-500 mt-1 mb-8">Find an expert for every home need.</p>
        <div className="flex gap-4 overflow-x-auto pb-4 [&::-webkit-scrollbar]:hidden">
          {services.map((s) => (
            <div
              key={s.label}
              className="min-w-[180px] bg-white rounded-2xl border border-slate-100 shadow-sm p-6 flex flex-col items-center gap-3 hover:shadow-md hover:border-emerald-200 transition-all shrink-0 cursor-pointer"
            >
              <div className={`rounded-xl p-3 ${s.iconBg}`}>
                <s.icon className="w-6 h-6" />
              </div>
              <span className="text-slate-700 text-sm font-medium">{s.label}</span>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

function WhySection() {
  const reasons = [
    {
      title: 'Verified Professionals',
      desc: 'Every specialist goes through background checks and skill verification before joining our platform.',
    },
    {
      title: 'Instant Booking',
      desc: 'Book a specialist in seconds with real-time availability. No back-and-forth calls needed.',
    },
    {
      title: 'Satisfaction Guaranteed',
      desc: "If you're not happy with the service, we'll make it right. Your satisfaction is our priority.",
    },
  ]
  return (
    <section className="py-20">
      <div className="max-w-7xl mx-auto px-6">
        <h2
          className="text-2xl font-bold text-slate-900 mb-12 text-center"
          style={{ fontFamily: "'Manrope', sans-serif" }}
        >
          Why SnapServe
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-10">
          {reasons.map((r) => (
            <div key={r.title}>
              <CheckCircle className="w-8 h-8 text-emerald-600 mb-4" />
              <h3
                className="text-lg font-bold text-slate-900"
                style={{ fontFamily: "'Manrope', sans-serif" }}
              >
                {r.title}
              </h3>
              <p className="text-slate-500 text-sm mt-2 leading-relaxed">{r.desc}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

function StatsSection() {
  return (
    <section className="border-y border-slate-100 py-16">
      <div className="max-w-7xl mx-auto px-6 grid grid-cols-2 md:grid-cols-4 gap-8">
        {stats.map((s) => (
          <div key={s.label} className="text-center">
            <p
              className="text-4xl font-extrabold text-emerald-600"
              style={{ fontFamily: "'Manrope', sans-serif" }}
            >
              {s.value}
            </p>
            <p className="text-slate-500 text-sm mt-1">{s.label}</p>
          </div>
        ))}
      </div>
    </section>
  )
}

function HowItWorksSection() {
  return (
    <section className="py-20 bg-slate-50">
      <div className="max-w-7xl mx-auto px-6">
        <h2
          className="text-2xl font-bold text-slate-900 mb-16 text-center"
          style={{ fontFamily: "'Manrope', sans-serif" }}
        >
          How It Works
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-12">
          {steps.map((step) => (
            <div key={step.number} className="relative text-center">
              <span
                className="text-[6rem] font-extrabold text-emerald-50 leading-none absolute top-0 left-1/2 -translate-x-1/2 select-none pointer-events-none"
                style={{ fontFamily: "'Manrope', sans-serif" }}
              >
                {step.number}
              </span>
              <div className="relative pt-16">
                <h3
                  className="text-xl font-bold text-slate-900"
                  style={{ fontFamily: "'Manrope', sans-serif" }}
                >
                  {step.title}
                </h3>
                <p className="text-slate-500 text-sm mt-2 leading-relaxed">{step.description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

function SpecialistsSection() {
  return (
    <section className="py-20">
      <div className="max-w-7xl mx-auto px-6">
        <h2
          className="text-2xl font-bold text-slate-900 mb-12 text-center"
          style={{ fontFamily: "'Manrope', sans-serif" }}
        >
          Top Specialists
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {specialists.map((sp) => (
            <div
              key={sp.name}
              className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6"
            >
              <div className="w-14 h-14 rounded-full bg-gradient-to-br from-emerald-500 to-teal-500 text-white font-bold text-lg flex items-center justify-center mb-4">
                {sp.initials}
              </div>
              <h3 className="font-bold text-slate-900">{sp.name}</h3>
              <p className="text-slate-500 text-sm">{sp.title}</p>
              <div className="flex items-center gap-0.5 mt-3">
                {[1, 2, 3, 4, 5].map((i) => (
                  <Star key={i} className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                ))}
                <span className="text-slate-500 text-sm ml-1.5">
                  {sp.rating} ({sp.reviews})
                </span>
              </div>
              <Link
                to="/signup"
                className="mt-4 block text-center border border-emerald-600 text-emerald-600 rounded-xl py-2 w-full text-sm font-semibold hover:bg-emerald-50 transition-colors"
              >
                Book Now
              </Link>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

function TestimonialsSection() {
  return (
    <section className="py-20 bg-slate-50">
      <div className="max-w-7xl mx-auto px-6">
        <h2
          className="text-2xl font-bold text-slate-900 mb-12 text-center"
          style={{ fontFamily: "'Manrope', sans-serif" }}
        >
          What Our Customers Say
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {testimonials.map((t) => (
            <div
              key={t.author}
              className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6"
            >
              <div className="flex items-center gap-0.5 mb-4">
                {[1, 2, 3, 4, 5].map((i) => (
                  <Star key={i} className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                ))}
              </div>
              <p className="text-slate-600 text-sm italic leading-relaxed mb-4">"{t.quote}"</p>
              <p className="font-semibold text-slate-900 text-sm">{t.author}</p>
              <p className="text-slate-400 text-xs">{t.role}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

function HomeFooter() {
  return (
    <footer className="bg-slate-900 py-12" style={{ fontFamily: "'Manrope', sans-serif" }}>
      <div className="max-w-7xl mx-auto px-6">
        <div className="flex flex-col md:flex-row items-center justify-between gap-6">
          <Link to="/" className="text-white font-extrabold text-xl">
            SnapServe
          </Link>
          <div className="flex items-center gap-8">
            <Link
              to="/specialists"
              className="text-slate-300 text-sm hover:text-white transition-colors"
            >
              Browse Services
            </Link>
            <Link to="/login" className="text-slate-300 text-sm hover:text-white transition-colors">
              Login
            </Link>
            <Link
              to="/signup"
              className="text-slate-300 text-sm hover:text-white transition-colors"
            >
              Sign Up
            </Link>
          </div>
        </div>
        <div className="mt-8 pt-8 border-t border-slate-800">
          <p className="text-slate-500 text-xs text-center">
            &copy; 2024 SnapServe. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  )
}

export default function HomePage() {
  const { isAuthenticated, user } = useAuthStore()

  if (isAuthenticated && user) {
    return <Navigate to={`/${user.role}/dashboard`} replace />
  }

  return (
    <div className="min-h-screen bg-white" style={{ fontFamily: "'Manrope', sans-serif" }}>
      <style>{`@import url('https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap');`}</style>
      <Navbar />
      <HeroSection />
      <ServicesSection />
      <WhySection />
      <StatsSection />
      <HowItWorksSection />
      <SpecialistsSection />
      <TestimonialsSection />
      <HomeFooter />
    </div>
  )
}
