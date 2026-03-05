import { Link } from 'react-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'

const mockSpecialists = [
  { id: '1', name: 'John Doe', title: 'Electrician', rating: 4.8 },
  { id: '2', name: 'Jane Smith', title: 'Plumber', rating: 4.9 },
  { id: '3', name: 'Mike Johnson', title: 'Carpenter', rating: 4.7 },
]

export default function BrowseSpecialists() {
  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Browse Specialists</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {mockSpecialists.map((specialist) => (
          <Card key={specialist.id}>
            <CardContent className="p-4">
              <h3 className="font-semibold text-lg">{specialist.name}</h3>
              <p className="text-slate-600">{specialist.title}</p>
              <p className="text-sm text-slate-500 mb-3">Rating: {specialist.rating}/5</p>
              <Link to={`/specialists/${specialist.id}`}>
                <Button variant="outline" size="sm">
                  View Profile
                </Button>
              </Link>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  )
}
