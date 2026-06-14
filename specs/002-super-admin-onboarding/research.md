# Research: F02 Super Admin Bootstrap and Clinic Onboarding

## Decision: Use Java 21 With Spring Boot 3.3.5

**Rationale**: The active backend `pom.xml`, F01 plan, Maven compiler configuration, and Spring
Boot 3.3.x compatibility path use Java 21. `TECH_STACK.md` still contains a project-coordinate row
listing Java 25, but its Maven properties and current repository implementation use Java 21.

**Alternatives considered**:
- Java 25: rejected for F02 because it conflicts with the current backend build and would turn this
  feature into a platform upgrade.
- Leave unresolved: rejected because planning must resolve technical context before tasks.

## Decision: Extend The Existing Modular Monolith

**Rationale**: F02 builds directly on F01 authentication, audit, request ID, tenancy, and response
foundation. A single backend transaction must create clinic profile, defaults, WhatsApp metadata,
first Clinic Admin, permissions, credential state, public slug reservation, and audit events.

**Alternatives considered**:
- Separate onboarding service: rejected because microservices are forbidden for MVP and would
  complicate the all-or-nothing transaction.
- Direct repository access across modules: rejected by the constitution; services must own business
  rules and cross-module interactions.

## Decision: Flyway Bootstrap For Initial SUPER_ADMIN

**Rationale**: F02 requires the first platform operator to exist after migration/setup. Flyway is
already required by `TECH_STACK.md` and F01. The migration must be idempotent, avoid duplicate
SUPER_ADMIN records, and use a non-live bootstrap secret source strategy resolved during
implementation.

**Alternatives considered**:
- Manual database insert: rejected because it is not repeatable and is unsafe for new
  environments.
- Application startup seeding only: rejected as the primary mechanism because schema and bootstrap
  state should be traceable through migration history; startup validation may still protect
  misconfiguration.

## Decision: SUPER_ADMIN Sessions Carry No Clinic Scope

**Rationale**: The constitution requires SUPER_ADMIN to be the only cross-tenant role and to act
through explicit platform/support routes. F02 onboarding and deactivation are platform actions, so
the session identifies actor authority while the target clinic is created or selected by the
platform operation, not by a token clinic claim.

**Alternatives considered**:
- Put a selected clinic in SUPER_ADMIN JWT: rejected because it risks stale or implicit
  cross-tenant authority.
- Reuse clinic-user tenant routes for SUPER_ADMIN: rejected because clinic scope would be
  ambiguous and audit would be weaker.

## Decision: Single Transaction For Onboarding Durable State

**Rationale**: The spec requires the clinic, defaults, public slug, WhatsApp metadata, first Clinic
Admin, permissions, credential state, and success audit events to commit or roll back together.
Spring service transactions over PostgreSQL satisfy this while keeping audit consistent with
committed state.

**Alternatives considered**:
- Multi-step wizard that commits each section independently: rejected because partial clinics would
  block later features and complicate cleanup.
- Async/default background creation: rejected because platform operators need a deterministic
  success/failure result.

## Decision: Database Uniqueness Plus Service-Level Conflict Mapping

**Rationale**: Duplicate clinic slug, first Clinic Admin email, WhatsApp display number, and
WhatsApp phone number ID must fail cleanly even under concurrent submissions. Database unique
constraints are the source of truth; service validation provides friendly early errors and maps
constraint violations to field-specific conflict responses.

**Alternatives considered**:
- Service-only uniqueness checks: rejected because concurrent requests can race.
- Allow duplicate email across clinics: rejected for MVP because login ownership would be
  ambiguous.

## Decision: Idempotent-Safe Onboarding And Deactivation

**Rationale**: The constitution requires retry-safe mutating operations. SUPER_ADMIN users may
double-click or retry after timeouts. F02 should use an idempotency key/request fingerprint for
onboarding and make deactivation repeat-safe so duplicate submissions do not create duplicate
clinics, users, credentials, audit success events, or token revocations.

**Alternatives considered**:
- Rely on unique constraints only: rejected because it turns legitimate retries into confusing
  conflicts and may regenerate credentials.
- Ignore retries in UI only: rejected because backend must be authoritative.

## Decision: One-Time Temporary Credential Display

**Rationale**: The first Clinic Admin needs an initial credential, but temporary passwords must not
be recoverable from logs, audit, browser storage, repeated responses, or later API reads. The
backend returns it once after successful onboarding; only a hash/forced-reset state is retained.

**Alternatives considered**:
- Email the password automatically: rejected because email delivery is outside F02 scope.
- Store the temporary password for later viewing: rejected by the spec and security posture.

## Decision: Encrypt WhatsApp Credential Placeholders At Rest

**Rationale**: The constitution treats WhatsApp credentials and tokens as secrets. F02 stores WABA
metadata and token placeholders for future WhatsApp features but does not call WhatsApp APIs. Any
token-like or secret placeholder must be encrypted at rest and excluded from repeatable responses,
logs, and audit metadata.

**Alternatives considered**:
- Plaintext placeholder storage: rejected because future credentials could be mishandled and it
  violates the constitution.
- Defer all WhatsApp metadata storage: rejected because F02 explicitly onboards WhatsApp metadata
  and enforces uniqueness.

## Decision: Minimal React/Vite SUPER_ADMIN Onboarding UI

**Rationale**: `feature.md` includes a minimal onboarding UI. `TECH_STACK.md` locks React,
TypeScript, TailwindCSS, Vite, Axios, Zod, TanStack Query, and Testing Library. The UI should be a
focused operator workflow rather than a full dashboard.

**Alternatives considered**:
- Backend-only F02: rejected because `feature.md` includes UI acceptance checks.
- Full SUPER_ADMIN dashboard: rejected because F02 explicitly excludes it.

## Decision: Contract-First API And UI Artifacts

**Rationale**: F02 exposes new backend routes and a frontend onboarding flow. OpenAPI documents
platform APIs, and a UI contract documents form sections, states, safe persistence rules, and
frontend expectations without embedding implementation code.

**Alternatives considered**:
- Only prose in plan.md: rejected because tasks and tests need stable endpoints/states.
- Generate full implementation code during planning: rejected because implementation belongs to
  `/speckit-tasks` and `/speckit-implement`.

## Decision: Testcontainers PostgreSQL For F02 Integration Validation

**Rationale**: F02 depends on PostgreSQL uniqueness, transactions, Flyway migrations, encryption
storage behavior, and concurrent conflicts. Testcontainers already supports F01 validation and
keeps CI/developer runs reproducible.

**Alternatives considered**:
- H2-only integration tests: rejected because it may not match PostgreSQL constraint and migration
  behavior.
- Manual local PostgreSQL only: rejected because validation must be repeatable in CI and agent
  environments.
