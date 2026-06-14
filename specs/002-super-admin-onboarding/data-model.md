# Data Model: F02 Super Admin Bootstrap and Clinic Onboarding

## Entity: SuperAdminUser

Platform operator account used for onboarding and deactivation.

**Fields**
- `id`: UUID.
- `email`: globally unique login identifier.
- `passwordHash`: hashed credential; never returned or logged.
- `role`: `SUPER_ADMIN`.
- `clinicId`: always null.
- `status`: active, suspended, or deleted.
- `forcePasswordReset`: boolean, normally false for an active configured platform operator.
- `createdAt`, `updatedAt`, `deletedAt`, `deletedBy`: audit and soft-delete metadata.

**Relationships**
- Has many refresh sessions.
- Creates audit events as actor.

**Validation Rules**
- At least one active `SUPER_ADMIN` must exist.
- SUPER_ADMIN permissions are role-derived and not editable permission rows.
- SUPER_ADMIN sessions carry no clinic scope.
- Adding additional SUPER_ADMIN users or removing access is not implemented in F02 except for the
  bootstrap guarantee and rejection of clinic-user elevation attempts.

## Entity: Clinic

Tenant root created by SUPER_ADMIN onboarding.

**Fields**
- `id`: UUID.
- `name`: required display name.
- `slug`: required unique, lower-case, URL-safe tenant/public identifier.
- `address`: clinic address text or structured address payload.
- `country`: ISO-style country code or configured country value.
- `timezone`: IANA timezone name used for clinic-local date boundaries.
- `defaultLanguage`: default clinic communication language.
- `primaryPhone`: clinic contact phone; redacted in logs/audit.
- `email`: clinic contact email.
- `status`: active or inactive.
- `logoMetadata`: safe file metadata only; no binary upload is required by F02.
- `createdAt`, `updatedAt`, `deactivatedAt`, `deactivatedBy`, `deactivationReason`.

**Relationships**
- Has many clinic users.
- Has one clinic settings record.
- Has many operating hour records.
- Has reminder defaults, FAQ defaults, public website slug reservation, and WhatsApp metadata.

**Validation Rules**
- `slug` is unique, lower-case, URL-safe, and platform-controlled.
- `timezone` must be a valid IANA timezone.
- `status = inactive` prevents clinic-user refresh and later clinic-scoped access.
- Deactivation requires a SUPER_ADMIN actor and non-empty reason.

**State Transitions**
- Active -> Inactive through SUPER_ADMIN deactivation.
- Inactive -> Active is not part of F02.

## Entity: ClinicSettings

Default operational settings created during onboarding.

**Fields**
- `id`: UUID.
- `clinicId`: owning clinic.
- `slotDurationMinutes`: one of 15, 20, 30, 45, or 60.
- `maxAdvanceBookingDays`: default 30, allowed range 7 to 90.
- `minBookingNoticeMinutes`: default 60, allowed range 15 to 1440.
- `cancellationCutoffMinutes`: default 120, allowed range 0 to 1440.
- `rescheduleCutoffMinutes`: default 120 unless configured otherwise.
- `defaultLanguage`: copied from clinic onboarding input.
- `publicWebsiteEnabled`: initial enable flag for later public website features.
- `createdAt`, `updatedAt`.

**Relationships**
- Belongs to one clinic.

**Validation Rules**
- Values must satisfy constitution booking/cutoff ranges even though booking itself is out of
  scope.
- Created in the same onboarding transaction as the clinic.

## Entity: OperatingHour

Clinic-local weekly operating windows created during onboarding.

**Fields**
- `id`: UUID.
- `clinicId`: owning clinic.
- `dayOfWeek`: Monday through Sunday.
- `openTime`: local clinic time.
- `closeTime`: local clinic time.
- `closed`: boolean.
- `createdAt`, `updatedAt`.

**Relationships**
- Belongs to one clinic.

**Validation Rules**
- Open days require `openTime < closeTime`.
- Times are interpreted in the clinic timezone.
- At least one open operating window is required for an active clinic unless explicitly configured
  as closed for setup-only onboarding.

## Entity: ReminderDefault

Default appointment reminder schedule created for later notification features.

**Fields**
- `id`: UUID.
- `clinicId`: owning clinic.
- `type`: `MORNING_OF` or `BEFORE_APPOINTMENT`.
- `enabled`: boolean.
- `sendTimeLocal`: clinic-local time for morning-of reminder, default 08:00.
- `offsetMinutes`: relative offset for pre-appointment reminder, default 120.
- `createdAt`, `updatedAt`.

**Relationships**
- Belongs to one clinic.

**Validation Rules**
- F02 stores defaults only; it does not create notification records or send reminders.
- Reminder values must be valid for later scheduler consumption.

## Entity: FaqSeed

Default FAQ entry created for later clinic customization and WhatsApp FAQ automation.

**Fields**
- `id`: UUID.
- `clinicId`: owning clinic.
- `category`: platform-approved FAQ category.
- `question`: safe seed question.
- `answer`: safe seed answer.
- `enabled`: boolean.
- `createdAt`, `updatedAt`.

**Relationships**
- Belongs to one clinic.

**Validation Rules**
- Seed content must not contain PHI.
- Created in onboarding transaction.

## Entity: PublicWebsiteSlugReservation

Reserved public-facing slug for later website features.

**Fields**
- `id`: UUID.
- `clinicId`: owning clinic.
- `slug`: unique, lower-case, URL-safe public slug.
- `enabled`: boolean.
- `reservedAt`: timestamp.
- `createdAt`, `updatedAt`.

**Relationships**
- Belongs to one clinic.

**Validation Rules**
- Slug uniqueness must be enforced by database constraint.
- F02 does not expose public website pages or booking endpoints.

## Entity: WhatsAppMetadata

Per-clinic WhatsApp Business metadata captured during onboarding.

**Fields**
- `id`: UUID.
- `clinicId`: owning clinic.
- `wabaId`: WhatsApp Business Account identifier.
- `phoneNumberId`: unique WhatsApp phone number identifier used for later tenant resolution.
- `displayPhoneNumber`: unique display number; redacted in logs/audit.
- `templateNamespace`: template namespace or business template namespace placeholder.
- `encryptedAccessTokenPlaceholder`: encrypted token-like value or placeholder.
- `encryptedAppSecretPlaceholder`: encrypted secret-like value or placeholder when provided.
- `createdAt`, `updatedAt`.

**Relationships**
- Belongs to one clinic.

**Validation Rules**
- `phoneNumberId` is globally unique.
- `displayPhoneNumber` is globally unique.
- Token-like and secret-like values are encrypted at rest.
- F02 does not register templates, process webhooks, or send messages.

## Entity: ClinicAdminUser

First tenant-scoped administrator created during onboarding.

**Fields**
- `id`: UUID.
- `clinicId`: newly created clinic.
- `email`: globally unique login identifier.
- `fullName`: admin display name.
- `phone`: optional contact phone; redacted in logs/audit.
- `passwordHash`: hash of generated temporary password.
- `role`: `CLINIC_ADMIN`.
- `status`: active pending first-login reset.
- `forcePasswordReset`: true at creation.
- `temporaryCredentialIssuedAt`: timestamp of one-time issuance.
- `createdAt`, `updatedAt`.

**Relationships**
- Belongs to one clinic.
- Has default permissions.
- Has refresh sessions after login.

**Validation Rules**
- Must not be able to create or elevate SUPER_ADMIN users.
- Cannot access normal clinic operations until first-login password reset completes.
- Temporary password is returned once only and never stored in recoverable plain text.

**State Transitions**
- PendingFirstLoginReset -> Active after successful password reset.
- Active -> Suspended/Deleted is deferred to F03 user management.

## Entity: DefaultPermissionAssignment

Initial permission assignment for the first Clinic Admin.

**Fields**
- `id`: UUID.
- `clinicId`: owning clinic.
- `userId`: first Clinic Admin user.
- `module`: clinic module name.
- `level`: permission level such as admin/read-write/read/none.
- `createdAt`, `updatedAt`.

**Relationships**
- Belongs to one clinic and one user.

**Validation Rules**
- First Clinic Admin receives default admin permissions for clinic management modules.
- SUPER_ADMIN permissions are not stored here.
- Detailed clinic staff permission editing is deferred to F03.

## Entity: OnboardingRequestRecord

Idempotency and audit support record for onboarding submissions.

**Fields**
- `id`: UUID.
- `idempotencyKey`: client-supplied or server-derived unique key for retry safety.
- `requestFingerprint`: safe hash of normalized non-secret onboarding input.
- `actorUserId`: SUPER_ADMIN actor.
- `status`: in-progress, succeeded, failed.
- `clinicId`: created clinic when succeeded.
- `safeResultSummary`: safe non-secret result metadata.
- `createdAt`, `updatedAt`, `completedAt`.

**Relationships**
- Created by one SUPER_ADMIN.
- May point to the created clinic.

**Validation Rules**
- Repeated successful submissions with the same key/fingerprint must not create duplicate durable
  records or temporary credentials.
- Secrets and temporary passwords are not stored in this record.

## Entity: RefreshSession

F01 revocable session record affected by F02 deactivation.

**Fields**
- `id`: UUID.
- `userId`: clinic user.
- `revokedAt`: timestamp when revoked.
- `revokedReason`: includes clinic deactivation reason category, not sensitive free text.

**Relationships**
- Belongs to one user.
- Users belong to clinic.

**State Transitions**
- Active/rotated -> Revoked when owning clinic is deactivated.

## Entity: AuditEvent

PHI-redacted event record for F02 platform actions.

**New event categories**
- Bootstrap SUPER_ADMIN available.
- Bootstrap skipped because active SUPER_ADMIN already exists.
- SUPER_ADMIN onboarding submitted.
- Clinic onboarding succeeded.
- Clinic onboarding failed.
- Clinic created.
- First Clinic Admin created.
- Default permissions assigned.
- Public website slug reserved.
- WhatsApp metadata captured.
- Temporary credential issued.
- Clinic deactivated.
- Clinic refresh sessions revoked.
- Clinic user attempted SUPER_ADMIN creation/elevation.

**Validation Rules**
- Metadata excludes raw phone numbers, emails where not required, tokens, secrets, temporary
  passwords, hashes, request bodies, stack traces, and PHI.
- Success audit events for onboarding share the onboarding transaction.
- Failed validation/security events may be recorded with safe metadata and request ID.
