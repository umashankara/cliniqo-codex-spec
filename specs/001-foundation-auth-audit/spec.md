# Feature Specification: F01 Foundation, Tenancy, Auth, Audit

**Feature Branch**: `001-foundation-auth-audit`

**Created**: 2026-06-13

**Status**: Draft

**Input**: User description: "Implement F01 Foundation, Tenancy, Auth, Audit for Cliniqo AI. Build a Spring Boot modular monolith foundation with PostgreSQL, Flyway, UUID IDs, soft-delete base entity, audit columns, JWT auth, refresh tokens, TenantContext from JWT, WebsiteContext placeholder, explicit SUPER_ADMIN clinic route context placeholder, global API envelope, typed exceptions, request ID propagation, PHI-safe structured logging, and a centralized audit module. Enforce that clinic users never supply clinicId from request bodies, cross-tenant access returns 404, audit writes happen in the same transaction as the parent action, and integration tests cover Clinic A attempting to access Clinic B data. Exclude clinic onboarding UI, appointments, WhatsApp, OpenAI, and analytics screens. Reference feature.md."

## Clarifications

### Session 2026-06-13

- Q: Should F01 include the frontend auth shell from feature.md or remain backend-only as requested here? -> A: Backend foundation only; frontend auth shell is deferred to a later feature/spec.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Authenticate Clinic Users Safely (Priority: P1)

Clinic staff and platform operators need a secure sign-in foundation that issues short-lived access
and refresh sessions, carries the correct actor identity, and rejects unauthenticated access before
any clinic-scoped work can occur.

**Why this priority**: Every later clinic workflow depends on trusted identity and session state.
Without this, tenant isolation, auditability, and role-aware behavior cannot be trusted.

**Independent Test**: A user can sign in, receive a valid authenticated session, access only
permitted protected resources, refresh the session, and lose access after logout or session
revocation.

**Acceptance Scenarios**:

1. **Given** a valid clinic user, **When** the user signs in with correct credentials, **Then** the
   system returns an authenticated session that includes actor identity and clinic scope.
2. **Given** an expired access session with a valid refresh session, **When** the client requests a
   session refresh, **Then** the system issues a new authenticated session without changing clinic
   scope.
3. **Given** a logged-out, revoked, or invalid session, **When** the client calls a protected
   resource, **Then** access is rejected with a typed, non-sensitive error response.

---

### User Story 2 - Enforce Clinic Tenant Isolation (Priority: P1)

Clinic users need every clinic-scoped operation to use trusted tenant context so that one clinic can
never read, list, update, delete, or infer another clinic's data.

**Why this priority**: Cross-clinic leakage is a critical product and trust failure for a healthcare
automation platform.

**Independent Test**: With test fixtures for Clinic A and Clinic B, a Clinic A user can access
Clinic A data but receives tenant-safe not-found responses when attempting direct, list, update, or
delete access to Clinic B data.

**Acceptance Scenarios**:

1. **Given** a Clinic A authenticated user and a Clinic B record identifier, **When** the user tries
   to read the Clinic B record, **Then** the response is `404 Not Found` and reveals no existence,
   count, or ownership detail.
2. **Given** a Clinic A authenticated user, **When** a request body includes a `clinicId` value,
   **Then** the system ignores the supplied value, uses trusted Clinic A context, and records a
   tenancy tamper audit event.
3. **Given** no trusted clinic context, **When** a clinic-scoped resource is invoked, **Then** the
   request is rejected before business processing.

---

### User Story 3 - Record Auditable Foundation Activity (Priority: P2)

Compliance reviewers and platform operators need security-sensitive actions, tenant tampering, and
future business changes to produce durable audit events that commit or roll back with the parent
action.

**Why this priority**: Cliniqo must be able to explain who did what, when, and in which clinic
without leaking protected information.

**Independent Test**: A successful mutating action creates its expected audit event in the same
durable unit of work, while a failed or rolled-back action leaves no orphan audit record.

**Acceptance Scenarios**:

1. **Given** a mutating clinic-scoped action succeeds, **When** the action commits, **Then** its
   audit event is committed with the same actor, clinic, request ID, event type, and redacted
   metadata.
2. **Given** a mutating clinic-scoped action fails and rolls back, **When** audit records are
   reviewed, **Then** no committed audit event claims that the failed action succeeded.
3. **Given** a security validation failure such as tenant tampering, **When** the system rejects or
   neutralizes the request, **Then** an audit event records the safe security context without PHI.

---

### User Story 4 - Return Consistent Safe Service Errors (Priority: P3)

Client applications and support staff need every response and failure to follow a predictable shape
with request IDs, typed errors, and no stack traces, secrets, or PHI.

**Why this priority**: Consistent responses reduce client defects and make incidents diagnosable
without exposing protected content.

**Independent Test**: Successful responses, validation failures, authentication failures,
authorization failures, tenant-safe not-found responses, and unexpected errors all include a request
ID and a consistent structure with safe messages.

**Acceptance Scenarios**:

1. **Given** any successful protected operation, **When** the response is returned, **Then** it uses
   the standard response envelope with a request ID.
2. **Given** validation, authentication, authorization, not-found, or unexpected failure, **When**
   the response is returned, **Then** it uses a typed error form with safe details and no stack
   trace.
3. **Given** an operational log emitted during a request, **When** logs are inspected, **Then** the
   log contains request and safe actor context but excludes PHI, secrets, tokens, phone numbers, and
   patient message content.

### Edge Cases

- Clinic A user targets a Clinic B record by direct identifier: return tenant-safe not-found and
  reveal no ownership or existence details.
- Clinic A user attempts list or search operations that could include Clinic B data: include only
  Clinic A results and reveal no cross-clinic counts.
- Request body includes `clinicId`, alternate tenant hints, or mismatched route context: ignore
  untrusted tenant input for clinic users and audit the tamper attempt.
- Clinic-scoped resource is called without trusted tenant context: reject before business logic or
  persistence runs.
- Refresh session is expired, revoked, reused after logout, or belongs to a deactivated actor:
  reject with a typed authentication error and no sensitive detail.
- SUPER_ADMIN attempts clinic work without an explicit clinic route context: reject the request
  because cross-tenant scope must be explicit.
- Audit event creation fails for a mutating action: roll back the parent action so the committed
  state is never missing its audit record.
- Unexpected failures occur after partial validation: return safe error responses and logs with
  request ID only, not stack traces or protected content.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide authenticated access sessions and refresh sessions for platform
  and clinic users.
- **FR-002**: System MUST derive clinic user tenant identity only from trusted authenticated server
  context.
- **FR-003**: System MUST reject clinic-scoped operations that lack trusted clinic context.
- **FR-004**: System MUST provide a placeholder website context for future public clinic booking
  scope without enabling public booking behavior in this feature.
- **FR-005**: System MUST require SUPER_ADMIN clinic support operations to use an explicit target
  clinic route context before any cross-tenant action is allowed.
- **FR-006**: System MUST ignore `clinicId` and equivalent tenant hints supplied by clinic users in
  request bodies, query payloads, or client-controlled input.
- **FR-007**: System MUST audit tenant tampering whenever a clinic user supplies client-controlled
  tenant identity.
- **FR-008**: System MUST return `404 Not Found` for clinic users attempting to access another
  clinic's records, without leaking whether the record exists.
- **FR-009**: System MUST provide a common base for business records that supports globally unique
  identifiers, creation/update audit columns, and soft deletion.
- **FR-010**: System MUST keep tenant-exempt records explicitly documented and limited to platform
  infrastructure records such as clinics, refresh sessions, migration history, and static lookup
  data.
- **FR-011**: System MUST return all successful and failed client-facing service responses in a
  consistent global envelope that includes the request ID.
- **FR-012**: System MUST provide typed, safe error categories for validation, authentication,
  authorization, not-found, tenancy, conflict, and unexpected failures.
- **FR-013**: System MUST propagate a request ID through request handling, response envelopes,
  audit records, and structured logs.
- **FR-014**: System MUST prevent PHI, secrets, credentials, tokens, phone numbers, patient message
  bodies, and stack traces from appearing in responses or logs.
- **FR-015**: System MUST provide centralized audit records for authentication events, refresh
  session changes, tenant tamper events, SUPER_ADMIN target-clinic context use, soft-delete actions,
  and other security-sensitive foundation actions.
- **FR-016**: System MUST commit audit records in the same durable transaction as the parent
  mutating action.
- **FR-017**: System MUST roll back parent mutating actions when required audit writes fail.
- **FR-018**: System MUST include test fixtures for at least two clinics and users scoped to each
  clinic.
- **FR-019**: System MUST include integration tests proving Clinic A users cannot read, list,
  update, delete, or infer Clinic B data.
- **FR-020**: System MUST include automated checks that reject clinic-user request contracts that
  accept `clinicId` for tenant-scoped operations.
- **FR-021**: System MUST exclude frontend auth shell work, clinic onboarding UI, appointment
  booking, WhatsApp integration, OpenAI integration, and analytics screens from this feature.

### Key Entities *(include if feature involves data)*

- **Clinic**: A tenant boundary for clinic-scoped users and future business records.
- **User**: An authenticated actor with platform or clinic role context and active/inactive status.
- **Refresh Session**: A revocable long-lived session record used to renew authenticated access.
- **Tenant Context**: Trusted clinic scope derived from the authenticated clinic user session.
- **Website Context**: Reserved public website clinic scope placeholder for later booking features.
- **SUPER_ADMIN Route Context**: Explicit target-clinic context used only by platform operators on
  support routes.
- **Soft-Deletable Business Record**: Shared record shape with unique identifier, audit columns,
  and deletion metadata.
- **Audit Event**: Durable, PHI-redacted record of security-sensitive and mutating activity.
- **Request Trace**: Request ID and safe request metadata used to connect responses, logs, and
  audit records.

### Constitution Alignment *(mandatory)*

- **Tenant Isolation**: Clinic scope is derived from trusted session context only. Clinic users
  never provide authoritative `clinicId`; cross-tenant access returns tenant-safe not-found; and
  SUPER_ADMIN support scope requires explicit target-clinic route context.
- **Appointment/Channel Lifecycle**: Appointment, WhatsApp, OpenAI, public booking, and analytics
  workflows are excluded. This feature establishes the transaction, audit, tenant, and soft-delete
  contracts those later workflows must use.
- **PHI/Security/Audit**: The feature handles credentials, sessions, actor identity, request IDs,
  and security metadata. It requires PHI-safe responses, PHI-safe structured logs, centralized
  audit events, soft-delete metadata, and same-transaction audit writes.
- **Module Boundaries**: Affects the foundation areas for authentication, clinic context,
  platform route context, common response/error handling, logging, persistence base records, and
  audit. Later domain modules must consume these through explicit service or context interfaces.
- **Required Tests**: Requires authentication/session tests, request ID and error envelope tests,
  PHI-safe logging tests, audit transaction tests, soft-delete base behavior tests, request-contract
  checks for forbidden `clinicId`, and Clinic A versus Clinic B cross-tenant integration tests for
  read, list, update, delete, and inference attempts.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of protected clinic-scoped requests without trusted clinic context are rejected
  before business processing.
- **SC-002**: 100% of Clinic A attempts to access Clinic B fixture data through read, list, update,
  delete, or inference paths return tenant-safe not-found or Clinic A-only results.
- **SC-003**: 100% of clinic-user request contracts checked by the foundation contain no
  authoritative `clinicId` field for tenant-scoped operations.
- **SC-004**: 100% of successful mutating foundation actions that require audit records commit the
  audit record with the parent action, and 0 rolled-back actions leave a successful audit record.
- **SC-005**: 100% of error responses include a request ID, safe typed error category, and no stack
  trace, credential, token, phone number, patient message content, or PHI.
- **SC-006**: Security and tenant-isolation test suites for the foundation pass in automated
  validation before downstream feature work begins.

## Assumptions

- F01 is backend foundation only for this specification; frontend auth shell work is deferred to a
  later feature/spec.
- Later frontend auth shell work can be specified separately or added by amendment if desired.
- Clinic onboarding, first-clinic creation, and first-admin bootstrap are deferred to F02.
- Appointment, WhatsApp, OpenAI, and analytics features will use this foundation but are not
  implemented or exposed here.
- Tenant-exempt tables must be intentionally documented during planning and reviewed against the
  constitution.
