# snapserve — agent brief
snapserve is a local-service booking platform: customers discover professionals such as plumbers
and electricians, compare fixed-price or hourly offerings, and request appointments; professionals
publish offerings and accept or decline requests. One Spring Boot API and one React app live in
this monorepo.

## The Project
Core promise: booking-first. The MVP has no payments, complaints, admin approval workflows, or
email notifications; notifications wait until the booking domain emits application events. There is
no API gateway, service discovery, Feign, or separately deployed service — the backend stays one
deployable modular monolith and splits only if a real need appears.
Status: in-flight — the revival is stalled after the foundation (schema, security, app shell)
landed; most domain modules and API endpoints are still planned.
Non-negotiables: one primary role per account (`CUSTOMER` or `PROVIDER`, extensible later), and
booking addresses are snapshotted so later profile edits never rewrite history.

## How I Work Here
- Verification: run `bun run verify` before handoff; CI owns the full suite, so add focused tests only for what you touched.
- Commits: Conventional Commits; the allowed types live in `commitlint.config.js` (enforced by husky). Work on a branch, never commit to main, and land changes through a PR.
- Secrets: environment variables only, documented in `.env.example` — never hardcode a value.
- Strict TypeScript is non-negotiable; never relax compiler settings to get code through.
- Booking transitions belong in the booking application service, never controllers (state machine: README § Architecture).
- Trap: Vite serves on 5173 while the API's default CORS origin is `http://localhost:3000`; set `CORS_ALLOWED_ORIGINS` or run Vite on 3000 before calling the API from the browser.

## Sources of Truth
| Fact | Where |
|---|---|
| Product intent | § The Project (this file) |
| Runtime behavior | code + tests |
| Commands & deps | `package.json`, `backend/snapserve-api/build.gradle.kts` |
| Commit rules | `commitlint.config.js` |
| Architecture & planned API | `README.md` § Architecture |
| Schema & secrets | `db/migration/`, `.env.example` |
Code and tests are the authority; this file never overrides executable truth.
