# Implementation Plan: F01 Foundation, Tenancy, Auth, Audit

**Branch**: `001-foundation-auth-audit` | **Date**: 2026-06-13 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-foundation-auth-audit/spec.md`

**Note**: Technical choices are sourced from [TECH_STACK.md](../../TECH_STACK.md) and constrained by
the Cliniqo AI constitution.

## Summary

Build the backend-only foundation for Cliniqo AI as a Spring Boot modular monolith. The work creates
the Maven project shell, PostgreSQL/Flyway persistence baseline, UUID/audit/soft-delete base
records, JWT access and refresh sessions, trusted `TenantContext`, placeholder `WebsiteContext`,
placeholder SUPER_ADMIN target-clinic route context, global response/error envelopes, request ID
propagation, PHI-safe structured logging, and centralized same-transaction audit events. F01 does
not implement frontend auth shell, clinic onboarding, appointments, WhatsApp, OpenAI, analytics, or
full SUPER_ADMIN support workflows.

## Technical Context

**Language/Version**: Java 21 for Spring Boot 3.3.x compatibility. Note: `TECH_STACK.md` project
coordinates list Java 25, while its Maven properties, initialization commands, and compatibility
matrix list Java 21. This plan uses Java 21 as the executable target and records the conflict in
research.

**Primary Dependencies**: Spring Boot 3.3.5; Spring Web; Spring Data JPA/Hibernate; Spring
Validation; Spring Security; Spring Actuator; PostgreSQL driver 42.7.3; Flyway 10.17.0 plus
PostgreSQL module; JJWT 0.12.6; Springdoc OpenAPI 2.6.0; MapStruct 1.6.0; Lombok optional;
logstash-logback-encoder 7.4; Caffeine 3.1.8 if configuration caching is introduced.

**Storage**: PostgreSQL 15+ with Flyway migrations, shared database tenancy using `clinic_id` on
business records, UUID primary keys, audit columns, and soft delete. Test storage uses
Testcontainers PostgreSQL for integration tests; H2 may be kept only for narrow unit-style tests
that do not validate PostgreSQL behavior.

**Testing**: JUnit 5, Spring Boot Test, MockMvc, Spring Security Test, Testcontainers PostgreSQL,
and focused architecture/contract checks for forbidden clinic-user `clinicId` request contracts and
tenant-blind repository patterns where feasible.

**Target Platform**: Backend jar deployable to Railway/Docker for MVP; local development via Maven
and Docker-backed PostgreSQL.

**Project Type**: Backend web service within a modular monolith. No frontend implementation in F01.

**Performance Goals**: Authentication, context resolution, response envelope, request ID, and audit
foundation paths complete within 300 ms p95 under normal MVP load. Cross-tenant denial and typed
error responses complete within 300 ms p95 and do not perform unnecessary business work.

**Constraints**: No client-supplied tenant authority for clinic users; cross-tenant clinic-user
access returns 404; all required audit writes share the parent transaction; logs and responses are
PHI-safe; SUPER_ADMIN tokens have no clinic scope and require explicit target-clinic route context;
SUPER_ADMIN route context is only a foundation placeholder in F01.

**Scale/Scope**: Foundation must support all later MVP modules but only implements backend
infrastructure, auth/session primitives, tenant context, audit, error/logging contracts, and
fixtures for two-clinic tenant isolation tests.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Tenant isolation**: PASS. Clinic tenant identity is derived from JWT-backed `TenantContext`
  only. Clinic-user request contracts must not accept authoritative `clinicId`. Cross-tenant access
  returns 404 with no IDs, counts, or ownership details. `WebsiteContext` and SUPER_ADMIN target
  route context are placeholders only; public booking and support data access are deferred.
- **Appointment/channel lifecycle**: PASS. F01 does not touch appointments, patient communication,
  WhatsApp, OpenAI, notification dispatch, or analytics. It establishes transaction and audit
  primitives later appointment features must use.
- **Modular monolith boundaries**: PASS. Affected packages are `common`, `config`, `auth`,
  `clinic` foundation context, `superadmin` context placeholder, `publicwebsite` context
  placeholder, and `audit`. Controllers validate and delegate; services own transactions; DTOs are
  API contracts; repositories do persistence only.
- **PHI/security/audit**: PASS. The feature handles credentials, JWTs, refresh sessions, request
  IDs, actor IDs, clinic IDs, and audit metadata. Responses and logs must redact PHI, secrets,
  tokens, phone numbers, patient message bodies, and stack traces. Audit writes for parent actions
  are same-transaction.
- **Tests and observability**: PASS. Required tests include auth/session, request ID propagation,
  global error envelope, audit transaction rollback, PHI-safe logging, soft delete, forbidden
  `clinicId` request contracts, and Clinic A versus Clinic B read/list/update/delete/inference
  integration tests.

## Project Structure

### Documentation (this feature)

```text
specs/001-foundation-auth-audit/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── foundation-api.openapi.yaml
└── tasks.md
```

### Source Code (repository root)

```text
backend/
├── pom.xml
├── src/main/java/com/cliniqo/
│   ├── CliniqoApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── OpenApiConfig.java
│   │   └── RequestIdFilter.java
│   ├── common/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── logging/
│   │   └── security/
│   ├── auth/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── audit/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── clinic/
│   │   └── context/
│   ├── publicwebsite/
│   │   └── context/
│   └── superadmin/
│       └── context/
├── src/main/resources/
│   ├── application.yml
│   ├── logback-spring.xml
│   └── db/migration/
└── src/test/java/com/cliniqo/
    ├── auth/
    ├── audit/
    ├── common/
    ├── tenancy/
    └── support/
```

**Structure Decision**: Use a single backend Maven project under `backend/` with package
`com.cliniqo`, matching `TECH_STACK.md`. F01 intentionally omits `frontend/` source creation.
Future features may add inactive domain packages as needed, but F01 implementation tasks should
stay scoped to foundation packages above.

## Complexity Tracking

No constitution violations or complexity exceptions are required.

## Phase 0: Research Summary

See [research.md](./research.md). All technical unknowns are resolved for planning. The only stack
conflict is Java 25 versus Java 21 in `TECH_STACK.md`; this plan selects Java 21 because it matches
Spring Boot 3.3.x compatibility guidance and the stack doc's own Maven/init sections.

## Phase 1: Design Summary

See [data-model.md](./data-model.md), [foundation-api.openapi.yaml](./contracts/foundation-api.openapi.yaml),
and [quickstart.md](./quickstart.md).

### Post-Design Constitution Check

- **Tenant isolation**: PASS. Data model separates clinic-scoped `BusinessRecord` from
  tenant-exempt `AuditableRecord`; contracts model clinic-user calls with no body `clinicId`;
  tests require two-clinic leak coverage.
- **Appointment/channel lifecycle**: PASS. No appointment/channel contracts are introduced.
- **Modular monolith boundaries**: PASS. Contracts and data model map to foundation modules only.
- **PHI/security/audit**: PASS. Audit metadata is redacted, request IDs are mandatory, and
  log/response redaction is part of quickstart validation.
- **Tests and observability**: PASS. Quickstart requires automated validation for auth, tenant
  isolation, audit transactionality, request IDs, and PHI-safe logs.
