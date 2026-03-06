# Commit Conventions

All commits must follow the [Conventional Commits](https://www.conventionalcommits.org/) specification.

## Commit Message Format

```
<type>[(optional scope)]: <description>

[optional body]

[optional footer(s)]
```

## Types

| Type | Description | Example |
|------|-------------|---------|
| `feat` | New feature | `feat(auth): add password reset` |
| `fix` | Bug fix | `fix(booking): correct date validation` |
| `docs` | Documentation | `docs(api): update endpoint docs` |
| `style` | Code style (formatting) | `style: fix indentation` |
| `refactor` | Code restructuring | `refactor(user): extract service layer` |
| `perf` | Performance improvement | `perf(query): add database index` |
| `test` | Adding/updating tests | `test(auth): add login tests` |
| `chore` | Maintenance tasks | `chore: update dependencies` |
| `ci` | CI/CD changes | `ci: add deployment workflow` |
| `build` | Build system changes | `build: upgrade gradle version` |
| `revert` | Revert previous commit | `revert: abc123` |

## Scopes

Scopes identify the affected area:

**Backend:**
- `auth` - Auth service
- `user` - User service
- `booking` - Booking service
- `gateway` - API Gateway
- `notification` - Notification service
- `common` - Shared libraries

**Frontend:**
- `frontend` - General frontend changes
- `ui` - UI components
- `auth-ui` - Auth-related UI
- `customer` - Customer portal
- `specialist` - Specialist portal

**Infrastructure:**
- `docker` - Docker configuration
- `ci` - CI/CD workflows
- `deps` - Dependencies

## Examples

### Feature Commits

```bash
# New feature with scope
feat(auth): implement refresh token rotation

# Feature without scope
feat: add email notifications for bookings

# Feature with body
feat(booking): allow customers to cancel bookings

- Add cancellation endpoint
- Send notification on cancellation
- Update booking status to CANCELLED
```

### Bug Fixes

```bash
# Simple fix
fix(user): correct email validation regex

# Fix with body
fix(auth): resolve token expiry check

The token expiration was checking against wrong timestamp,
causing tokens to appear valid after expiry.

Fixes #123
```

### Documentation

```bash
docs(api): document booking endpoints

docs(frontend): update component usage examples
```

### Breaking Changes

Add `BREAKING CHANGE:` in footer:

```bash
feat(auth): migrate to new JWT library

BREAKING CHANGE: Token format changed. All clients must update.
```

Or use `!` after type:

```bash
feat(auth)!: change authentication flow
```

## Commit Message Guidelines

### Description

- Use present tense: "add feature" not "added feature"
- Use imperative mood: "move cursor to..." not "moves cursor to..."
- Don't capitalize first letter
- No period at the end
- Max 72 characters for description line

### Body

- Explain what and why, not how
- Wrap at 72 characters
- Use bullet points for multiple items
- Reference issues: `Fixes #123`, `Closes #456`

### Footer

- **Fixes:** Reference issues being fixed
- **Refs:** Reference related issues
- **BREAKING CHANGE:** Document breaking changes
- **Co-authored-by:** Credit co-authors

## Examples by Type

### Backend Changes

```bash
feat(auth): add account lockout after failed logins

Implement security feature to lock accounts after 5 failed
login attempts. Locked accounts can be unlocked by admin
or after 30 minutes.

Fixes #45
```

```bash
fix(user): handle null phone number in profile

Customer profiles created before phone requirement
were causing NPE. Added null check and default value.

Refs #78
```

```bash
refactor(booking): extract validation logic to service

Move booking validation from controller to dedicated
BookingValidationService for better separation of concerns.
```

```bash
test(auth): add unit tests for token service

- Test token generation
- Test token validation
- Test token refresh
- Test edge cases
```

### Frontend Changes

```bash
feat(customer): add booking history page

Implement customer dashboard page showing past and
upcoming bookings with filtering and pagination.
```

```bash
fix(ui): correct button hover state in dark mode

Button background was not changing on hover in dark
mode due to incorrect Tailwind class order.
```

```bash
refactor(frontend): migrate to React Query for server state

Replace manual fetch calls with React Query for better
caching, background updates, and error handling.
```

### Infrastructure Changes

```bash
chore(deps): upgrade Spring Boot to 4.0.3

Update all services to Spring Boot 4.0.3 for security
patches and new features.
```

```bash
ci: add frontend type checking to pipeline

Run TypeScript type checker in CI to catch type errors
before merging.
```

```bash
docker: optimize image layers

Reorder Dockerfile instructions to better utilize
cache layers and reduce build time.
```

## Validation

Commit messages are validated by commitlint in pre-commit hooks:

```bash
# Commitlint will reject these:
git commit -m "updated stuff"           # No type
git commit -m "feat:"                   # Empty description
git commit -m "FIX(auth): login"        # Uppercase type
git commit -m "feat(auth): login."      # Trailing period
```

## Workflow

1. **Write good commit messages** as you code
2. **Use meaningful scopes** to help reviewers
3. **Reference issues** in footers when applicable
4. **Keep commits atomic** - one logical change per commit
5. **Squash fixups** before merging (if using fixup commits)

## Tips

- Use `git commit -m "type(scope): description"` for simple commits
- Use `git commit` without `-m` to open editor for multi-line commits
- Consider using `git commit --fixup=HEAD` for quick fixes
- Run `git log --oneline` to see your commit history

## Common Mistakes to Avoid

❌ **Don't:**
- Mix multiple unrelated changes in one commit
- Write vague descriptions like "fix stuff" or "update code"
- Use past tense ("fixed", "added", "changed")
- Include issue numbers in the subject line
- Exceed 72 characters in the subject line

✅ **Do:**
- Keep commits focused and atomic
- Write clear, descriptive messages
- Use present tense and imperative mood
- Put issue references in the body/footer
- Break long subjects with the body
