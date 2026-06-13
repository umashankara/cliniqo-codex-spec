# Research: F01 Foundation, Tenancy, Auth, Audit

## Decision: Java 21 Runtime Target

**Rationale**: `TECH_STACK.md` contains conflicting Java references: project coordinates list Java
25, while Maven properties, Spring Initializr command, and compatibility matrix list Java 21. Spring
Boot 3.3.x is explicitly compatible with Java 21, and Java 21 is an LTS release suitable for MVP
deployment on common managed platforms.

**Alternatives considered**:
- Java 25: rejected for F01 planning because it conflicts with the stack doc's own compatibility
  matrix and may increase deployment/tooling risk.
- Leave unresolved: rejected because `/speckit-plan` must resolve technical context before tasks.

## Decision: Spring Boot Modular Monolith Backend Only

**Rationale**: The active F01 spec clarifies backend foundation only. Use one Maven project with
domain packages and service boundaries rather than multiple deployables. This satisfies the
constitution's modular monolith rule and keeps transactions local for audit and tenancy guarantees.

**Alternatives considered**:
- Microservices: forbidden by constitution for MVP.
- Frontend auth shell in F01: deferred by clarification to a later feature/spec.

## Decision: PostgreSQL 15+ With Flyway 10.17.0

**Rationale**: PostgreSQL is required by `TECH_STACK.md` and the constitution. Flyway enforces
repeatable schema evolution. UUID primary keys, tenant discriminator columns, refresh sessions,
audit events, and soft-delete indexes must be expressed in migrations.

**Alternatives considered**:
- H2-only development schema: rejected for integration behavior because tenant filters, UUIDs,
  indexes, and transaction semantics must match PostgreSQL.
- Manual schema management: rejected because all schema changes must be migrations.

## Decision: Shared Database Tenant Context From JWT

**Rationale**: Clinic users derive tenant scope from authenticated claims and server-side user
state. Clinic users never supply authoritative `clinicId`. Tenant isolation is enforced at request
context, service, repository/filter, and database predicate/index levels.

**Alternatives considered**:
- Request-body or query `clinicId`: rejected by constitution and spec.
- Separate database per clinic: rejected as unnecessary complexity for MVP.

## Decision: SUPER_ADMIN Context Contract Only In F01

**Rationale**: F01 creates the explicit target-clinic route context placeholder and audit contract.
Actual cross-clinic support reads/writes, platform dashboards, purge, and audit filtering belong to
F17. SUPER_ADMIN tokens carry no clinic scope; explicit support routes provide target context.

**Alternatives considered**:
- Implement minimal cross-clinic reads now: rejected by user confirmation to leave scope unchanged.
- Omit SUPER_ADMIN route context entirely: rejected because later support features need a safe
  foundation contract.

## Decision: Refresh Session Rotation With Reuse Detection

**Rationale**: The spec requires revocable refresh sessions and safe logout/revocation behavior.
For a healthcare-adjacent platform, rotating refresh sessions with reuse detection reduces replay
risk and creates clear audit events for suspicious reuse. A session family identifier supports
family revocation after token reuse.

**Alternatives considered**:
- Reuse same refresh token until expiry: simpler, but weaker replay detection.
- Rotate only the reused token: weaker incident containment than family revocation.

## Decision: Global Envelope With Typed Error Catalog

**Rationale**: All client-facing responses include request ID and either data or a typed safe error.
Typed categories enable frontend/API clients and tests to distinguish validation, authentication,
authorization, tenant-safe not-found, conflict, and unexpected failures without exposing stack
traces or PHI.

**Alternatives considered**:
- Raw Spring errors: rejected because they risk inconsistent shape and sensitive detail leakage.
- Free-form message-only errors: rejected because tests and clients need stable categories.

## Decision: Same-Transaction Audit Writes

**Rationale**: The spec requires audit records to commit or roll back with parent actions. Audit
service APIs must run inside the caller's transaction for durable state changes, while rejected
security validations that do not mutate parent state may record their own security audit event.

**Alternatives considered**:
- Async audit queue: rejected for F01 parent-action audit because it can drift from committed state.
- Separate audit transaction for all events: rejected for mutating parent actions because it can
  produce orphan success events after rollback.

## Decision: PHI-Safe Structured Logging

**Rationale**: `TECH_STACK.md` specifies Logback MDC keys for request, clinic, user, and action,
plus a custom redaction converter. F01 must implement request ID propagation and redaction tests for
tokens, credentials, phone numbers, patient message bodies, and stack traces.

**Alternatives considered**:
- Plain console logs: rejected because operational diagnosis needs structured context.
- Logging raw request/response bodies: rejected by PHI and secret redaction rules.

## Decision: Testcontainers PostgreSQL For Integration Tests

**Rationale**: Cross-tenant access, soft delete, Flyway migrations, indexes, and transaction/audit
behavior must be verified against PostgreSQL. Testcontainers provides repeatable integration tests
without requiring a shared developer database.

**Alternatives considered**:
- H2 for all tests: rejected because it may not match PostgreSQL behavior.
- Manual local database only: rejected because CI and agents need reproducible validation.
