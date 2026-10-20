# snapserve — agent brief
snapserve is a local-service booking platform: customers discover professionals like plumbers and
electricians, compare fixed-price or hourly offerings, and request appointments; professionals
publish offerings and accept or decline them. One Spring Boot API and one React app live here.

## The Project

Core promise: booking-first. The MVP has no payments, complaints, admin approval workflows, or
email notifications; notifications wait until the booking domain emits application events. There is
no API gateway, service discovery, Feign, or separately deployed service — the backend stays one
deployable modular monolith and splits only if a real need appears.

Status: in-flight — the revival is stalled after the foundation (schema, security, app shell)
landed; most domain modules and API endpoints are still planned.

Non-negotiables: one primary role per account (`CUSTOMER` or `PROVIDER`, extensible later), and
booking addresses are snapshotted so later profile edits never rewrite history.

## Sources of Truth

| Fact | Where |
|---|---|
| Product intent | § The Project (this file) |
| Runtime behavior | code + tests |
| Commands & deps | `package.json`, `backend/snapserve-api/build.gradle.kts` |
| How to work here | `CONTRIBUTING.md` |
| Architecture & planned API | `README.md` § Architecture |
| Schema & secrets | `db/migration/`, `.env.example` |

Code and tests are the authority; this file never overrides executable truth.
