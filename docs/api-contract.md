# SnapServe API contract

The API base path is `/api/v1`. Resource identifiers are UUIDs. Timestamps are ISO-8601 UTC
values. Errors use Spring's `application/problem+json` shape and include the request path.

## Planned MVP resources

| Area | Endpoints | Access |
| --- | --- | --- |
| Auth | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh` | Public |
| Professionals | `GET /professionals`, `GET /professionals/{id}` | Public |
| Offerings | `POST /professionals/me/offerings`, `PATCH /professionals/me/offerings/{id}` | Provider |
| Availability | `GET/PUT /professionals/me/availability` | Provider |
| Bookings | `POST /bookings`, `GET /bookings`, `GET /bookings/{id}` | Authenticated |
| Booking actions | `POST /bookings/{id}/accept`, `/decline`, `/cancel`, `/complete` | Role-dependent |
| Reviews | `POST /bookings/{id}/review`, `GET /professionals/{id}/reviews` | Role-dependent/public |

The endpoint list is a contract target for the next implementation slices; PR2 only establishes
the application, security, schema, and runtime foundation.

## Booking state machine

```text
REQUESTED -> CONFIRMED -> COMPLETED
     |            |
     v            v
  DECLINED     CANCELLED
```

Only the customer can create a request. Only the selected professional can confirm or decline a
request. Cancellation rules and who may cancel at each state will be encoded in the booking
application service, not inferred by controllers.
