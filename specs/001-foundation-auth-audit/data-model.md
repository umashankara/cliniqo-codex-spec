# Data Model: F01 Foundation, Tenancy, Auth, Audit

## Entity: Clinic

Tenant boundary record. F01 stores the minimum clinic data needed for fixtures and tenant context;
full onboarding fields are deferred to F02.

**Fields**
- `id`: UUID, primary identifier.
- `name`: display name for fixture/support context.
- `slug`: unique, URL-safe placeholder for future public website context.
- `status`: active or inactive.
- `createdAt`, `updatedAt`: audit timestamps.

**Relationships**
- Has many clinic users.
- Owns clinic-scoped business records in later features.

**Validation Rules**
- `slug` is unique and lower-case.
- Inactive clinics reject clinic-user authenticated access.

## Entity: User

Authenticated actor for clinic or platform access.

**Fields**
- `id`: UUID.
- `clinicId`: UUID for clinic users; null for SUPER_ADMIN users.
- `email`: unique login identifier.
- `passwordHash`: hashed secret, never logged or returned.
- `role`: `SUPER_ADMIN`, `CLINIC_ADMIN`, `RECEPTIONIST`, or `DOCTOR`.
- `status`: active, suspended, or deleted.
- `forcePasswordReset`: boolean reserved for onboarding flows.
- `createdAt`, `updatedAt`, `deletedAt`, `deletedBy`: audit and soft-delete metadata.

**Relationships**
- Belongs to one clinic when role is clinic-scoped.
- Has many refresh sessions.

**Validation Rules**
- Clinic roles require `clinicId`.
- `SUPER_ADMIN` requires null `clinicId` and uses explicit route context for clinic support work.
- Password hash, credentials, and tokens are never returned in service responses.

## Entity: RefreshSession

Revocable long-lived session used to refresh short-lived access.

**Fields**
- `id`: UUID.
- `userId`: authenticated actor.
- `tokenHash`: hash of the refresh token.
- `familyId`: UUID grouping rotated tokens.
- `issuedAt`, `expiresAt`, `lastUsedAt`: session lifecycle timestamps.
- `revokedAt`, `revokedReason`: revocation state.
- `replacedBySessionId`: next session when rotated.
- `requestId`, `createdIpHash`, `userAgentHash`: safe trace metadata.

**Relationships**
- Belongs to one user.
- May replace or be replaced by another refresh session in the same family.

**State Transitions**
- Active -> Rotated when used successfully.
- Active/Rotated -> Revoked on logout, deactivation, expiry cleanup, or reuse detection.
- Reuse detection revokes the session family and writes a security audit event.

## Entity: TenantContext

Per-request trusted clinic scope for clinic users.

**Fields**
- `clinicId`: trusted clinic UUID.
- `userId`: authenticated actor UUID.
- `role`: clinic role.
- `requestId`: current request trace.

**Validation Rules**
- Created only after authentication and active clinic/user validation.
- Not populated from request body, query payload, or client-controlled tenant hints.
- Required for clinic-scoped resources.

## Entity: WebsiteContext

Placeholder for future public website tenant scope.

**Fields**
- `clinicId`: resolved clinic UUID, unset until public website features.
- `slug`: public clinic slug, reserved for future resolution.
- `requestId`: current request trace.

**Validation Rules**
- F01 defines the placeholder only; no public booking behavior is implemented.

## Entity: SuperAdminRouteContext

Explicit target-clinic context for SUPER_ADMIN support routes.

**Fields**
- `actorUserId`: SUPER_ADMIN actor UUID.
- `targetClinicId`: explicit route clinic UUID.
- `requestId`: current request trace.
- `purpose`: support route/action category.

**Validation Rules**
- SUPER_ADMIN tokens do not carry clinic scope.
- Any cross-tenant support action must provide explicit target clinic route context.
- Actual cross-clinic data access workflows are deferred to F17.

## Entity: BusinessRecord

Mapped superclass/record contract for clinic-scoped business tables introduced by F01 and later
features.

**Fields**
- `id`: UUID.
- `clinicId`: immutable trusted clinic UUID.
- `createdAt`, `updatedAt`: timestamps.
- `createdBy`, `updatedBy`: safe actor/channel identifiers.
- `deletedAt`, `deletedBy`: soft-delete metadata.

**Validation Rules**
- `clinicId` is required for business records.
- Soft-deleted records are excluded from normal clinic-user reads.
- Purge is not exposed by F01.

## Entity: AuditableRecord

Base record for tenant-exempt infrastructure records.

**Fields**
- `id`: UUID.
- `createdAt`, `updatedAt`: timestamps.
- `createdBy`, `updatedBy`: safe actor identifiers.

**Validation Rules**
- Tenant-exempt use must be documented. F01 allowed examples: clinics, SUPER_ADMIN users, refresh
  sessions, Flyway history, audit events, static lookup data, and idempotency keys if introduced.

## Entity: AuditEvent

Write-mostly PHI-redacted record of security-sensitive and mutating activity.

**Fields**
- `id`: UUID.
- `eventType`: stable category such as authentication, refresh rotation, tenant tamper, soft
  delete, SUPER_ADMIN target context, or security validation failure.
- `actorUserId`: nullable for unauthenticated failures.
- `actorRole`: role at time of action.
- `clinicId`: affected clinic when applicable.
- `targetType`, `targetId`: safe target reference.
- `requestId`: request trace.
- `metadata`: redacted structured JSON.
- `occurredAt`: event timestamp.

**Validation Rules**
- Metadata must not include PHI, secrets, raw tokens, phone numbers, patient message bodies, or
  stack traces.
- Audit writes for parent mutating actions share the parent transaction.

## Entity: RequestTrace

Request-level diagnostic context.

**Fields**
- `requestId`: accepted inbound request ID or generated UUID.
- `clinicId`: safe MDC value when trusted.
- `userId`: safe MDC value when authenticated.
- `action`: safe action/category name.

**Validation Rules**
- Present in responses, logs, and audit events.
- Never contains raw request body, token, credential, PHI, or patient message content.
