# Implementation Plan: F02 Super Admin Bootstrap and Clinic Onboarding

**Branch**: `002-super-admin-onboarding` | **Date**: 2026-06-14 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/002-super-admin-onboarding/spec.md`

**Note**: Technical choices are sourced from [TECH_STACK.md](../../TECH_STACK.md), the current F01
foundation artifacts, and the Cliniqo AI constitution.

## Summary

Build the first platform-operator workflow on top of the F01 auth, tenancy, audit, response, and
logging foundation. F02 adds an idempotent Flyway bootstrap for the initial active `SUPER_ADMIN`,
hardens SUPER_ADMIN-only platform authorization, creates a transactional clinic onboarding service,
stores clinic defaults and encrypted WhatsApp credential placeholders, creates the first
`CLINIC_ADMIN` with forced first-login reset and one-time temporary credential display, reserves a
public website slug, writes PHI-safe audit events, revokes clinic refresh sessions on deactivation,
and adds the minimal SUPER_ADMIN onboarding UI. The feature intentionally excludes the full
platform dashboard, appointment booking, public website pages, real WhatsApp template registration,
webhooks, and live message sending.

## Technical Context

**Language/Version**: Java 21 backend target with Spring Boot 3.3.5. `TECH_STACK.md` project
coordinates list Java 25, but the active backend `pom.xml`, Maven compiler config, F01 plan, and
Spring Boot 3.3.x compatibility path use Java 21. Frontend uses TypeScript 5.6.3 with React 18.3.1
and Vite 5.4.8.

**Primary Dependencies**: Backend: Spring Web, Spring Data JPA/Hibernate, Spring Validation,
Spring Security, Spring Actuator, PostgreSQL driver 42.7.3, Flyway 10.17.0 with PostgreSQL module,
JJWT 0.12.6, Springdoc OpenAPI 2.6.0, MapStruct 1.6.0, Lombok optional,
logstash-logback-encoder 7.4, and existing Java crypto APIs for credential encryption. Frontend:
React, React Router, Axios, TanStack Query, Zod, TailwindCSS, Lucide React, date-fns, clsx, and
Vitest/Testing Library.

**Storage**: PostgreSQL 15+ via Flyway migrations. F02 extends the F01 schema with clinic
onboarding fields, clinic settings, operating hours, reminder defaults, FAQ seeds, public website
slug reservations, WhatsApp metadata with encrypted credential placeholders, default permission
records if not already present, onboarding idempotency state, and additional audit event types.
Test storage uses Testcontainers PostgreSQL; H2 is not sufficient for migration, uniqueness,
transaction rollback, encryption persistence, or concurrency checks.

**Testing**: Backend JUnit 5, Spring Boot Test, MockMvc, Spring Security Test, Testcontainers
PostgreSQL, focused contract checks for no clinic-user `clinicId` authority and SUPER_ADMIN
no-clinic-scope sessions. Frontend Vitest and Testing Library for the minimal onboarding UI,
credential display behavior, validation state, and PHI-safe browser behavior.

**Target Platform**: Backend jar deployable to Railway/Docker with Railway PostgreSQL. Frontend
Vite SPA deployable to Vercel and runnable locally against `localhost:8080`. Local development
uses Maven plus Docker-backed PostgreSQL; frontend uses npm/Vite.

**Project Type**: Full-stack web feature in a modular monolith backend plus a minimal React SPA
surface. The backend is authoritative for authorization, validation, idempotency, uniqueness,
transactionality, audit, and credential secrecy.

**Performance Goals**: SUPER_ADMIN onboarding submissions show a success or safe failure outcome
within 5 seconds for 95% of normal MVP-load requests. Duplicate-field failures return in under
1 second p95 after validation reaches the backend. Clinic deactivation and refresh-session
revocation complete within 5 seconds p95 for clinics with up to 100 users. UI validation gives
immediate client-side feedback for required fields while still deferring authority to backend
validation.

**Constraints**: SUPER_ADMIN tokens have no clinic scope. SUPER_ADMIN actions must use explicit
platform routes/action context and write PHI-safe audit events. Clinic users and Clinic Admins
cannot create or elevate SUPER_ADMIN users. Onboarding writes are all-or-nothing; audit writes for
successful parent actions share the parent transaction. Temporary passwords are shown once only and
are never logged, audited, stored in browser storage, or re-displayed. WhatsApp credential
placeholders and token-like values are encrypted at rest. No public website page, booking workflow,
webhook processing, WhatsApp template registration, live send, appointment booking, support
dashboard, purge, or analytics behavior is introduced.

**Scale/Scope**: F02 supports MVP clinic creation by platform operators for small clinics. It must
handle concurrent SUPER_ADMIN attempts for duplicate slug/email/WhatsApp identifiers, repeated
client submissions, and clinic deactivation for at least 100 clinic users. It introduces only the
minimal platform onboarding UI, not the broader SUPER_ADMIN dashboard.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Tenant isolation**: PASS. F02 creates clinic tenant boundaries and default clinic-owned data.
  Clinic users never supply authoritative `clinicId`; first Clinic Admin is bound to the newly
  created clinic by the onboarding transaction. SUPER_ADMIN sessions remain clinic-scope-free and
  use explicit platform routes/action context. Public website slug reservation is data-only; public
  route resolution and booking pages remain out of scope.
- **Appointment/channel lifecycle**: PASS. F02 stores operating hours, slot duration, booking
  windows, cutoff defaults, reminder defaults, and WhatsApp metadata for later features, but does
  not create appointments, notification records, public bookings, webhooks, AI actions, template
  registrations, or outbound WhatsApp sends.
- **Modular monolith boundaries**: PASS. Affected backend modules are `auth`, `clinic`,
  `superadmin`, `publicwebsite`, `whatsapp`, `notification` defaults, `audit`, `common`, and
  `config`. Controllers validate and delegate; services own transactions; repositories own
  persistence; DTOs define API contracts; frontend calls backend contracts through a shared client.
- **PHI/security/audit**: PASS. The feature handles staff credentials, clinic contact details,
  phone metadata, WhatsApp credential placeholders, temporary passwords, deactivation reasons,
  refresh session revocation, and SUPER_ADMIN authority. It requires encryption at rest for
  token-like WhatsApp values, one-time credential display, PHI-safe audit metadata, PHI-safe logs,
  no raw phone/token/password leakage, and same-transaction audit for successful mutating actions.
- **Tests and observability**: PASS. Required tests cover bootstrap idempotency, SUPER_ADMIN-only
  authorization, SUPER_ADMIN no-clinic-scope sessions, onboarding all-or-nothing rollback,
  duplicate slug/email/WABA phone/phone-number-id conflicts, retry/idempotent-safe submissions,
  encrypted credential storage, PHI-safe logs/audit/UI behavior, first-login reset, Clinic Admin
  cannot create SUPER_ADMIN, deactivation refresh-session revocation, and concurrency conflicts.

## Project Structure

### Documentation (this feature)

```text
specs/002-super-admin-onboarding/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── super-admin-onboarding-api.openapi.yaml
│   └── onboarding-ui.md
└── tasks.md
```

### Source Code (repository root)

```text
backend/
├── pom.xml
├── src/main/java/com/cliniqo/
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
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── mapper/
│   │   ├── repository/
│   │   └── service/
│   ├── common/
│   │   ├── dto/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── security/
│   │   └── crypto/
│   ├── notification/
│   │   ├── entity/
│   │   └── repository/
│   ├── publicwebsite/
│   │   ├── entity/
│   │   └── repository/
│   ├── superadmin/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── service/
│   │   └── repository/
│   └── whatsapp/
│       ├── entity/
│       └── repository/
├── src/main/resources/
│   └── db/migration/
└── src/test/java/com/cliniqo/
    ├── auth/
    ├── clinic/
    ├── superadmin/
    ├── audit/
    ├── common/
    └── support/

frontend/
├── package.json
├── vite.config.ts
├── tailwind.config.js
└── src/
    ├── app/
    ├── components/
    ├── features/superadmin/onboarding/
    ├── routes/
    ├── services/
    ├── stores/
    ├── types/
    └── test/
```

**Structure Decision**: Continue the F01 backend Maven project under `backend/` and add only the
frontend files required for authentication-aware SUPER_ADMIN onboarding. The backend remains the
source of truth for permissions, validation, transactionality, uniqueness, encryption, idempotency,
and audit. Frontend scope is limited to login reuse if needed, protected SUPER_ADMIN route guard,
onboarding form, safe error states, and one-time temporary credential display.

## Complexity Tracking

No constitution violations or complexity exceptions are required.

## Phase 0: Research Summary

See [research.md](./research.md). All technical unknowns are resolved for planning. The plan keeps
Java 21 despite the Java 25 project-coordinate line in `TECH_STACK.md`, uses PostgreSQL/Flyway for
all schema changes, uses the existing F01 auth/audit/response foundation, adds minimal Vite SPA
surface per `TECH_STACK.md`, and treats live WhatsApp template registration and public website pages
as excluded.

## Phase 1: Design Summary

See [data-model.md](./data-model.md), [super-admin-onboarding-api.openapi.yaml](./contracts/super-admin-onboarding-api.openapi.yaml),
[onboarding-ui.md](./contracts/onboarding-ui.md), and [quickstart.md](./quickstart.md).

### Post-Design Constitution Check

- **Tenant isolation**: PASS. Data model makes `Clinic` the tenant root and keeps SUPER_ADMIN users
  clinic-scope-free. Contracts use platform routes for onboarding/deactivation and do not allow
  clinic users to provide authoritative `clinicId`. Public slug reservation is storage-only.
- **Appointment/channel lifecycle**: PASS. Contracts and data model create defaults and WhatsApp
  metadata only. No appointment, webhook, AI, notification dispatch, or template registration
  contract is introduced.
- **Modular monolith boundaries**: PASS. Contracts map to `superadmin`, `clinic`, `auth`, `audit`,
  `whatsapp`, `publicwebsite`, and default-setting modules through service boundaries. UI contract
  maps to a minimal `features/superadmin/onboarding` surface.
- **PHI/security/audit**: PASS. Data model requires encrypted WhatsApp token placeholders, one-time
  temporary credential display, non-recoverable temporary password responses, PHI-safe audit
  metadata, and refresh-session revocation on clinic deactivation.
- **Tests and observability**: PASS. Quickstart requires backend integration validation for
  bootstrap, authorization, transaction rollback, uniqueness, idempotent retry, encryption, audit,
  deactivation token revocation, plus frontend validation for credential display and safe browser
  behavior.
