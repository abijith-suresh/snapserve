# GitHub CI/CD Agent Context

GitHub Actions workflows for continuous integration.

## Quick Commands

```bash
# No local commands - runs on GitHub
# View status: https://github.com/{owner}/{repo}/actions
```

## Workflows

### Backend CI (`.github/workflows/backend-ci.yml`)

Triggered on PR changes to:
- `backend/**`
- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle/**`

**Jobs:**
1. **format-check**: `./gradlew spotlessCheck`
2. **build**: `./gradlew build -x test`
3. **test**: `./gradlew test`

### Frontend CI (`.github/workflows/frontend-ci.yml`)

Triggered on PR changes to:
- `frontend/**`

**Jobs:**
1. **lint**: `bun run lint`
2. **type-check**: `bun run type-check`
3. **build**: `bun run build`

## Key Files

| File | Purpose |
|------|---------|
| `workflows/backend-ci.yml` | Backend pipeline |
| `workflows/frontend-ci.yml` | Frontend pipeline |
| `dependabot.yml` | Automated dependency updates |

## Service-Specific Rules

1. **Fail fast**: Format checks run first.
2. **Parallel jobs**: Lint and type-check run in parallel.
3. **Dependency**: Build job requires lint + type-check to pass.
4. **Java 21**: Backend uses Temurin distribution.
5. **Bun latest**: Frontend uses latest Bun version.
6. **Frozen lockfile**: Frontend installs with `--frozen-lockfile`.
7. **Conventional commits**: PR titles must follow Conventional Commits.

## Commit Convention

PR titles must follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add new feature
fix: correct bug
docs: update documentation
style: formatting changes
refactor: code restructuring
test: add tests
chore: maintenance tasks
ci: CI/CD changes
```

Enforced by commitlint in pre-commit hooks.

## Local Pre-commit

Husky + lint-staged runs on local commits:
- Backend: Spotless format
- Frontend: Biome format

## Full Documentation

- [Architecture Overview](../../AGENTS.md)

## Notes

- No deployment workflows yet (add for CD)
- Consider adding SonarQube for code quality
- Consider adding security scanning (Trivy, Snyk)
