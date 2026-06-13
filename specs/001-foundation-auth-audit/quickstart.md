# Quickstart: F01 Foundation, Tenancy, Auth, Audit

## Prerequisites

- Java 21.
- Maven 3.9+.
- Docker running for Testcontainers PostgreSQL when running true PostgreSQL integration tests.
- PostgreSQL 15+ if running the service outside Testcontainers.

## Setup

```bash
cd backend
mvn test
```

Expected result: unit and integration tests pass, including Testcontainers-backed migration checks.

When using Colima on macOS, run:

```bash
DOCKER_HOST=unix://$HOME/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true mvn test
```

Latest local validation: `DOCKER_HOST=unix://$HOME/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true JAVA_HOME=/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home PATH=/usr/local/opt/openjdk@21/bin:$PATH mvn test` passed with PostgreSQL 16 Testcontainers and Flyway migrations: 27 tests, 0 failures, 0 errors, 0 skipped on 2026-06-14.

## Run Locally

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Expected result: the backend starts on port `8080`, Flyway migrations run, health endpoint is
available, and logs include request ID fields without PHI.

## Validate Contracts

Review [contracts/foundation-api.openapi.yaml](./contracts/foundation-api.openapi.yaml). The
auth request bodies must not accept `clinicId`. All responses must use the success/error envelope
with `meta.requestId`.

## Validation Scenarios

### 1. Authentication And Refresh

1. Load seeded/test users for Clinic A, Clinic B, and SUPER_ADMIN.
2. Sign in as a Clinic A user with `/api/v1/auth/login`.
3. Confirm the response includes access token, refresh token, user role, Clinic A scope, and
   `meta.requestId`.
4. Refresh with `/api/v1/auth/refresh`.
5. Confirm the old refresh token is no longer accepted and reuse revokes the session family.

### 2. Clinic A Cannot Access Clinic B

1. Use Clinic A credentials.
2. Request a Clinic A fixture record through the tenant probe and confirm success.
3. Request a Clinic B fixture record by direct ID and confirm `404 Not Found`.
4. List/search the tenant probe fixtures and confirm only Clinic A data appears.
5. Attempt update/delete/inference checks defined by integration tests and confirm no Clinic B
   existence signal leaks.

### 3. Client-Supplied Clinic ID Is Ignored

1. Authenticate as Clinic A.
2. Send a clinic-scoped request body containing `clinicId` for Clinic B.
3. Confirm the service uses Clinic A context, records a tenant tamper audit event, and never
   trusts the supplied value.

### 4. Audit Transactionality

1. Execute a successful mutating foundation action and confirm its audit event commits with the
   same request ID and actor.
2. Force the parent action to roll back.
3. Confirm no committed audit event claims the rolled-back action succeeded.

### 5. SUPER_ADMIN Route Context Placeholder

1. Authenticate as SUPER_ADMIN.
2. Call `/api/v1/super-admin/clinics/{clinicId}/context-probe`.
3. Confirm the explicit target clinic context is accepted and audited.
4. Confirm SUPER_ADMIN cannot perform clinic support work through normal clinic-scoped routes
   without explicit target clinic context.

### 6. PHI-Safe Errors And Logs

1. Trigger validation, unauthenticated, forbidden, tenant-safe not-found, conflict, and unexpected
   errors.
2. Confirm every response includes `meta.requestId` and a typed safe error.
3. Confirm logs include request ID plus safe user/clinic context and exclude raw tokens,
   credentials, phone numbers, patient message bodies, stack traces, and PHI.

## Expected Automated Test Groups

- `auth`: login, logout, refresh rotation, refresh reuse detection, revoked/deactivated user cases.
- `tenancy`: missing tenant context, Clinic A versus Clinic B read/list/update/delete/inference.
- `audit`: same-transaction commit/rollback and security audit events.
- `common`: response envelope, typed exceptions, request ID propagation, PHI redaction.
- `architecture`: forbidden `clinicId` request contracts and tenant-blind repository patterns.

## Implementation Notes

- F01 is backend-only. Do not add frontend auth shell files in this feature.
- SUPER_ADMIN support is limited to explicit target-clinic route context placeholders in F01.
  Cross-clinic support data access, purge, and platform audit browsing remain F17.
- Local validation requires Java 21 and Maven.
