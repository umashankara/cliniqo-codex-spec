# Quickstart: F02 Super Admin Bootstrap and Clinic Onboarding

## Prerequisites

- Java 21.
- Maven 3.9+.
- Docker running for Testcontainers PostgreSQL.
- Node 18+ and npm for the minimal React/Vite onboarding UI once frontend files are added.
- F01 foundation implemented and passing.

## Backend Validation

Run unit tests:

```bash
cd backend
mvn test
```

Run PostgreSQL-backed integration tests:

```bash
cd backend
mvn verify -DskipITs=false
```

When using Colima on macOS:

```bash
cd backend
DOCKER_HOST=unix://$HOME/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true mvn verify -DskipITs=false
```

Expected result: Flyway migrations apply, bootstrap SUPER_ADMIN is available exactly once,
onboarding transaction tests pass, uniqueness and concurrency tests pass, and no PHI/secret logging
tests fail.

## Frontend Validation

After frontend files exist:

```bash
cd frontend
npm install
npm test
npm run build
```

Expected result: the minimal SUPER_ADMIN onboarding route validates required fields, handles
duplicate-field responses, protects non-SUPER_ADMIN users, shows temporary credentials once, and
does not persist temporary passwords.

## Run Locally

Backend:

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Frontend:

```bash
cd frontend
npm run dev
```

Expected result: backend runs on `8080`, frontend runs on Vite's configured port, and the
onboarding UI can call `/api/v1/super-admin/onboarding/clinics` through the shared API client.

## Validate Contracts

- Review [contracts/super-admin-onboarding-api.openapi.yaml](./contracts/super-admin-onboarding-api.openapi.yaml).
- Review [contracts/onboarding-ui.md](./contracts/onboarding-ui.md).
- Confirm onboarding request bodies do not allow clinic users to supply authoritative `clinicId`.
- Confirm the one-time temporary password is returned only in the initial onboarding `201` response
  and is marked sensitive/one-time in the contract rather than `writeOnly`.
- Confirm WhatsApp placeholder fields remain write-only inputs and are not exposed in repeatable
  responses.
- Confirm `/auth/refresh` continues to use the F01 auth success envelope and only adds the F02
  typed `401 CLINIC_INACTIVE` rejection for inactive clinics.

## Validation Scenarios

### 1. Bootstrap SUPER_ADMIN

1. Start from a fresh migrated database with Flyway bootstrap placeholders configured for
   bootstrap email and bcrypt password hash.
2. Confirm exactly one active bootstrap SUPER_ADMIN exists with the configured email.
3. Confirm rerunning migrations/setup does not create duplicate SUPER_ADMIN accounts.
4. Confirm test/dev uses only the documented non-live bcrypt hash and no live bootstrap password is
   committed.
5. Confirm missing or invalid bootstrap placeholders fail safely in non-test environments.
6. Sign in as SUPER_ADMIN and confirm the session has no clinic scope.

### 2. SUPER_ADMIN-Only Protection

1. Sign in as a Clinic Admin.
2. Attempt clinic onboarding and clinic deactivation.
3. Confirm both requests return forbidden safe errors and create no clinic, user, setting, or
   success audit state.
4. Attempt to create or elevate a SUPER_ADMIN as Clinic Admin and confirm rejection before role
   changes.

### 3. Atomic Clinic Onboarding

1. Sign in as SUPER_ADMIN.
2. Submit a valid onboarding request with clinic profile, operating hours, settings, reminders,
   FAQ defaults, public website slug, WhatsApp metadata, and first Clinic Admin.
3. Confirm clinic, settings, operating hours, reminder defaults, FAQ defaults, public slug,
   WhatsApp metadata, first Clinic Admin, permissions, credential state, and audit events exist.
4. Force a required child write or audit write failure.
5. Confirm no partial onboarding state remains.

### 4. Duplicate And Concurrent Conflicts

1. Onboard a clinic successfully.
2. Retry onboarding with duplicate clinic slug.
3. Retry with duplicate first-admin email.
4. Retry with duplicate WhatsApp display phone number.
5. Retry with duplicate WhatsApp phone number ID.
6. Confirm each returns field-specific conflict feedback and creates no new clinic.
7. Run concurrent duplicate submissions and confirm only one succeeds.

### 5. Idempotent-Safe Retry

1. Submit onboarding with an idempotency key.
2. Repeat the same submission after success.
3. Confirm no duplicate clinic, user, public slug, WhatsApp metadata, temporary credential, or
   misleading audit success event is created.
4. Repeat deactivation with the same idempotency key and confirm the result remains safe.

### 6. Temporary Password And First Login

1. Complete onboarding.
2. Confirm the success response/UI shows the temporary password exactly once.
3. Repeat the request through an idempotent replay or read the onboarding result and confirm the
   temporary password is absent.
4. Refresh or navigate away and confirm the UI cannot recover it.
5. Sign in as the first Clinic Admin and confirm forced password reset is required before normal
   clinic access.
6. Confirm temporary password, password hash, and tokens do not appear in logs or audit metadata.

### 7. WhatsApp Credential Safety

1. Submit WhatsApp token/app-secret placeholders during onboarding.
2. Confirm stored values are encrypted at rest.
3. Confirm repeatable API responses do not return secret-like values.
4. Confirm logs and audit metadata redact phone numbers and secret-like fields.
5. Confirm no WhatsApp template registration or live API call occurs.

### 8. Clinic Deactivation

1. Onboard a clinic and create/sign in clinic users.
2. Deactivate the clinic as SUPER_ADMIN with a reason.
3. Confirm the clinic is inactive and all clinic-user refresh sessions are revoked.
4. Attempt refresh for affected users and confirm typed `401 CLINIC_INACTIVE`.
5. Confirm audit events include SUPER_ADMIN actor, target clinic, reason category, request ID, and
   refresh-session revocation summary without secrets.

### 9. Scope Guardrails

1. Confirm no appointment booking endpoints or UI are introduced.
2. Confirm no public website pages are introduced.
3. Confirm no WhatsApp webhook, live send, or template registration behavior is introduced.
4. Confirm no full SUPER_ADMIN dashboard or support console is introduced.

## Expected Automated Test Groups

- `superadmin`: bootstrap, authorization, no-clinic-scope sessions, onboarding, deactivation.
- `clinic`: clinic profile/defaults, operating hours, public slug reservation.
- `whatsapp`: metadata uniqueness and encrypted credential storage.
- `auth`: forced password reset and refresh rejection after clinic deactivation.
- `audit`: same-transaction onboarding events, failed validation/security events, redaction.
- `common`: idempotency, typed conflict errors, request ID propagation.
- `frontend`: onboarding form validation, field conflict display, loading/success/error states,
  one-time credential display, no browser persistence.

## Implementation Notes

- Use [data-model.md](./data-model.md) for entity and validation details.
- Use [contracts/super-admin-onboarding-api.openapi.yaml](./contracts/super-admin-onboarding-api.openapi.yaml)
  for backend API shape.
- Use [contracts/onboarding-ui.md](./contracts/onboarding-ui.md) for frontend behavior.
- Keep F02 limited to onboarding and deactivation. Full dashboard, public pages, appointment
  booking, WhatsApp template registration, webhook handling, and live sends belong to later
  features.
