# Contributing to snapserve

snapserve is a two-language monorepo: a Spring Boot API under `backend/` and a React + TypeScript
app under `frontend/`.

## Setup

- JDK 25 for Gradle. If your default JDK differs, point `JAVA_HOME` at a JDK 25 install or the
  backend build fails resolving `compileJava`.
- Bun for the frontend and root scripts.
- Copy `.env.example` to `.env` and set `POSTGRES_PASSWORD` and `JWT_SECRET`, then start PostgreSQL
  with Docker Compose.

The API runs at `http://localhost:8080`; Vite serves the frontend at `http://localhost:5173`.

## Quality gate

Run `bun run verify` from the repo root before handoff. It is the only gate that covers both the
Java backend and the React frontend. Add focused tests for what you touched; CI owns the full
suite.

## Commits and pull requests

- Branch from the latest `main`; never commit directly to `main`.
- Use Conventional Commits. Allowed types live in `commitlint.config.js`, and Husky enforces them.
- Keep each PR focused and leave merging to the owner. The expected merge strategy is squash.
- Secrets are environment variables only, documented in `.env.example`; never hardcode a value.

## Terminology

A **customer** books; a **provider** publishes offerings and accepts or declines. An **offering**
is `FIXED` or `HOURLY`. A **booking** is the appointment request and its state.

## Traps

- Booking transitions belong in the booking application service, never in controllers.
- Stay on Spring MVC: never WebFlux, `Mono`, or `Flux`.
- Stay a modular monolith. A new domain module is a package under `com.snapserve`, not a Gradle
  subproject, and cross-module calls go through application services, never another module's
  repositories or entities.
- Flyway owns schema changes; Hibernate never owns the schema.
- CORS lives in the security configuration; never add `@CrossOrigin` to controllers.
- Vite serves on 5173 while the API's default allowed CORS origin is `http://localhost:3000`. Set
  `CORS_ALLOWED_ORIGINS` or run Vite on 3000 before calling the API from the browser.
- Strict TypeScript is non-negotiable; never relax compiler settings to get code through.

## Changelog

This repo has no changelog; do not add one.
