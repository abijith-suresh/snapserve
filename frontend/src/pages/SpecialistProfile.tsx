import { ArrowLeft } from 'lucide-react'
import { Link, useParams } from 'react-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

export default function SpecialistProfile() {
  const { id } = useParams()

  return (
    <div>
      <Link
        to="/specialists"
        className="inline-flex items-center text-sm text-slate-600 hover:text-slate-900 mb-4"
      >
        <ArrowLeft className="h-4 w-4 mr-1" />
        Back to Browse
      </Link>

      <Card>
        <CardHeader>
          <CardTitle className="text-2xl">Specialist Profile</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div>
            <p className="text-slate-600">Specialist ID: {id}</p>
            <p className="mt-2">This is a public specialist profile page.</p>
          </div>

          <Button>Book Appointment</Button>
        </CardContent>
      </Card>
    </div>
  )
}
