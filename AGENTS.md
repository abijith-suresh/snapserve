# SnapServe — agent brief
SnapServe is a two-language monorepo — one Spring Boot API plus one React app — for booking local
services; this brief is for agents working in this checkout.

## The Project
Core promise: customers discover local professionals such as plumbers and electricians, compare
fixed-price or hourly offerings, and request appointments; professionals publish offerings and
accept or decline those requests.
Non-goals: payments, complaints, admin approval workflows, and email notifications (the booking
domain should emit application events before an email provider arrives). No API gateway, service
discovery, Feign, or separately deployed services.
Status: in-flight — the modular-monolith revival is stalled after the foundation (schema, security,
app shell) landed; most domain modules and API endpoints are still planned.
MVP shape: one primary role per account (`CUSTOMER` or `PROVIDER`, extensible later); offerings are
`FIXED` or `HOURLY`; booking addresses are snapshotted so later profile edits never rewrite history.

## How I Work Here
- Quality gate: `bun run verify` — runs the backend Gradle build (format check plus tests) and the frontend lint/type-check/build; run it before handoff.
- Commits: Conventional Commits; the type list lives in `commitlint.config.js` (husky `commit-msg` hook). Never commit to main; land work through a PR.
- No hardcoded secrets. Every value comes from environment variables documented in `.env.example`.
- Backend and frontend build with different toolchains and are run separately; see `backend/AGENTS.md` for the Java side.
- Frontend TypeScript is strict: `bun run type-check` must pass with zero errors. Biome owns lint and format, applied to staged files by lint-staged.
- API contract: base path `/api/v1`, UUID identifiers, ISO-8601 UTC timestamps, errors as `application/problem+json` including the request path. Bookings follow the state machine in README § Architecture; transitions live in the booking application service, never in controllers.
- Dev-server gotcha: Vite serves on 5173 by default while the API's CORS default is `http://localhost:3000`; set `CORS_ALLOWED_ORIGINS` (or run Vite on 3000) before calling the API from the browser.

## Sources of Truth
| Fact | Where |
|---|---|
| Product intent | § The Project (this file) |
| Runtime behavior | code + tests |
| Commands & deps | `package.json`, `frontend/package.json`, `backend/snapserve-api/build.gradle.kts` |
| Commit rules | `commitlint.config.js` |
| Schema & config | `db/migration/`, `application.yml`, `.env.example` |
Code and tests are the authority; this file never overrides executable truth.

## Getting Around
- `backend/` — Spring Boot modular monolith; the only Gradle module is `:backend:snapserve-api` (see `backend/AGENTS.md`).
- `frontend/` — React 19 + Vite app on Bun; features in `src/features/`, shadcn/ui primitives in `src/components/ui/`, pages and routes in `src/pages` and `src/routes`, HTTP client and types in `src/shared/`.
- `docker-compose.yml` — PostgreSQL 17 on 5432 and the API on 8080 for local runs.
- `.github/workflows/` — backend CI (`spotlessCheck`, `build -x test`, `test`) and frontend CI (lint, type-check, build).
- Root tooling — husky hooks in `.husky/` and staged-file rules in `.lintstagedrc.json`.
- Module responsibilities and the planned API surface — README § Architecture.
