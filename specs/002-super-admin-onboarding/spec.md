# Feature Specification: F02 Super Admin Bootstrap and Clinic Onboarding

**Feature Branch**: `002-super-admin-onboarding`

**Created**: 2026-06-14

**Status**: Draft

**Input**: User description: "Implement F02 Super Admin Bootstrap and Clinic Onboarding for Cliniqo AI. Seed the first SUPER_ADMIN with Flyway, protect SUPER_ADMIN-only APIs, and create an atomic onboarding flow that creates clinic profile, unique slug, timezone, operating hours, default language, settings, reminder defaults, FAQ defaults, public website slug, WhatsApp metadata, first Clinic Admin, default permissions, temporary password with forced first-login reset, and audit events. Enforce unique slug, clinic admin email, WABA phone number, and phone_number_id. Deactivating a clinic must revoke its users' refresh tokens. Exclude full platform dashboard, appointment booking, public website pages, and real WhatsApp template registration. Refer feature.md for more details on F02, and keep the generated spec maintainable and easy to understand."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Bootstrap Platform Administration (Priority: P1)

The platform owner needs the first SUPER_ADMIN account to exist after initial platform setup so a
trusted operator can sign in and begin creating clinics without manual data repair or unsafe
workarounds.

**Why this priority**: No clinic can be onboarded safely until there is at least one audited,
active platform operator with SUPER_ADMIN authority.

**Independent Test**: Start from an empty platform setup, verify exactly one configured bootstrap
SUPER_ADMIN can authenticate, and verify non-SUPER_ADMIN users cannot access platform-only
operations.

**Acceptance Scenarios**:

1. **Given** a fresh platform setup with no users, **When** setup completes, **Then** one active
   bootstrap SUPER_ADMIN exists and can authenticate through the normal sign-in flow.
2. **Given** an authenticated user without SUPER_ADMIN role, **When** the user attempts a
   platform-only onboarding or clinic-deactivation operation, **Then** access is denied before any
   clinic or user state changes.
3. **Given** the bootstrap SUPER_ADMIN signs in, **When** audit records are reviewed, **Then** the
   authentication and subsequent platform actions are traceable without exposing credentials.

---

### User Story 2 - Onboard a Clinic Atomically (Priority: P1)

A SUPER_ADMIN needs to create a clinic, its default operating configuration, WhatsApp metadata,
public website slug reservation, first Clinic Admin, default permissions, and onboarding audit
events in one reliable operation.

**Why this priority**: Clinic onboarding is the first revenue-enabling workflow. Partial clinics,
missing settings, or unaudited first-admin creation would block later booking, messaging, and
clinic management features.

**Independent Test**: Submit a complete onboarding request as SUPER_ADMIN and verify the clinic,
first Clinic Admin, default configuration, WhatsApp metadata, public slug reservation, default
permissions, temporary credential state, and audit records all exist together; then force one
required part to fail and verify none of the onboarding records are committed.

**Acceptance Scenarios**:

1. **Given** a SUPER_ADMIN provides valid clinic, settings, WhatsApp, public slug, and first-admin
   details, **When** onboarding is submitted, **Then** the clinic and all required onboarding
   records are created together and the first Clinic Admin receives a temporary password state that
   requires reset on first login.
2. **Given** any required onboarding record cannot be created, **When** onboarding fails, **Then**
   no partial clinic, first-admin account, settings, permissions, WhatsApp metadata, FAQ defaults,
   public slug reservation, or success audit event remains.
3. **Given** onboarding succeeds, **When** the SUPER_ADMIN views the result, **Then** the temporary
   password is available for one-time display only and is never exposed in audit records or logs.

---

### User Story 3 - Prevent Duplicate Clinic Identity and Channels (Priority: P1)

A SUPER_ADMIN needs immediate, safe feedback when onboarding data conflicts with an existing
clinic, first admin, or WhatsApp channel identity.

**Why this priority**: Duplicate slugs, admin emails, WhatsApp display numbers, or phone number
identifiers would break tenant resolution, public website routing, staff identity, and future
message handling.

**Independent Test**: Attempt clinic onboarding with duplicate slug, duplicate clinic admin email,
duplicate WABA display phone number, and duplicate WhatsApp phone number identifier; each attempt
fails cleanly with no partial onboarding state.

**Acceptance Scenarios**:

1. **Given** a clinic slug is already reserved, **When** onboarding is submitted with that slug,
   **Then** the request fails with a field-specific conflict and no new clinic is created.
2. **Given** a user email already belongs to any active or retained clinic admin account, **When**
   onboarding uses that email for the first Clinic Admin, **Then** onboarding fails with a
   field-specific conflict and no temporary credential is generated.
3. **Given** WhatsApp display number or phone number identifier is already assigned to another
   clinic, **When** onboarding reuses either value, **Then** onboarding fails with a field-specific
   conflict and no WhatsApp metadata is attached.

---

### User Story 4 - Deactivate a Clinic Safely (Priority: P2)

A SUPER_ADMIN needs to deactivate a clinic in a way that stops clinic users from continuing active
sessions while preserving auditability and future support visibility.

**Why this priority**: Deactivation is a high-impact platform action. Users from a deactivated
clinic must not continue operating through existing refresh sessions.

**Independent Test**: Deactivate an active clinic as SUPER_ADMIN and verify clinic users cannot
refresh sessions afterward, while audit records identify the actor, target clinic, reason, and
revocation outcome.

**Acceptance Scenarios**:

1. **Given** an active clinic with clinic users and refresh sessions, **When** a SUPER_ADMIN
   deactivates the clinic with a reason, **Then** the clinic becomes inactive and all refresh
   sessions for that clinic's users are revoked.
2. **Given** a user from a deactivated clinic has an old refresh session, **When** the user attempts
   to refresh authentication, **Then** the request is rejected with a safe authentication error.
3. **Given** clinic deactivation succeeds, **When** audit records are reviewed, **Then** they show
   the SUPER_ADMIN actor, target clinic, deactivation reason, and token revocation summary without
   exposing secrets.

### Edge Cases

- Bootstrap runs more than once: it must not create duplicate SUPER_ADMIN accounts or weaken the
  guarantee that at least one active SUPER_ADMIN exists.
- A non-SUPER_ADMIN user attempts onboarding, deactivation, or protected platform operations:
  reject before any business state or audit success event is created.
- Onboarding includes a slug with uppercase letters, spaces, unsupported characters, or leading or
  trailing separators: reject with clear validation or normalize only when the final reserved slug
  remains unambiguous and unique.
- Two SUPER_ADMIN users submit the same clinic slug, first-admin email, WhatsApp display number, or
  phone number identifier concurrently: only one succeeds and the other receives a safe conflict.
- Onboarding fails after some records were prepared: all durable onboarding state rolls back
  together, including clinic profile, settings, permissions, FAQ defaults, WhatsApp metadata, public
  slug reservation, first admin, temporary credential state, and success audit events.
- Temporary password generation or first-login-reset state fails: onboarding fails completely and
  no usable first-admin account remains.
- Temporary password is requested after the one-time display is consumed: it is not recoverable;
  the platform must require a fresh reset flow.
- WhatsApp token placeholders or metadata contain secrets: secrets are never logged or shown in
  audit details, and onboarding responses only expose safe metadata.
- Public website slug is reserved but public pages are excluded: slug lookup can be prepared for
  later features, but no public booking page is exposed by this feature.
- Clinic deactivation is retried after a partial or already completed request: the result remains
  safe, auditable, and does not leave active refresh sessions for that clinic.
- Clinic-local timezone changes are not part of this feature after onboarding; initial timezone is
  validated during creation and later edit flows belong to clinic settings features.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provision one initial active SUPER_ADMIN account during first platform
  setup when no equivalent account exists.
- **FR-002**: System MUST keep at least one active SUPER_ADMIN available and MUST NOT let bootstrap
  behavior create duplicate platform operators.
- **FR-003**: System MUST allow only authenticated SUPER_ADMIN users to create clinics, create the
  first Clinic Admin through onboarding, manage onboarding-only platform data, or deactivate
  clinics.
- **FR-004**: System MUST reject onboarding and deactivation attempts from non-SUPER_ADMIN users
  before changing clinic, user, credential, settings, WhatsApp, permission, or audit success state.
- **FR-005**: System MUST create the clinic profile during onboarding with name, slug, address,
  country, timezone, default language, operating hours, primary phone, email, slot duration, booking
  windows, cancellation and reschedule cutoffs, and logo metadata when provided.
- **FR-006**: System MUST enforce that clinic slugs are unique, URL-safe, lower-case, and reserved
  for one clinic only.
- **FR-007**: System MUST create default clinic settings required for later scheduling,
  communication, language, booking-window, cutoff, and operational behavior.
- **FR-008**: System MUST create default appointment reminder settings for the clinic, including
  morning-of and relative pre-appointment reminder defaults that later notification features can
  use.
- **FR-009**: System MUST create default FAQ records for the clinic from platform-approved seed
  content suitable for later clinic customization.
- **FR-010**: System MUST reserve a public website slug for the clinic and store whether public
  website booking is enabled, without exposing public website pages in this feature.
- **FR-011**: System MUST capture WhatsApp business metadata for the clinic, including WABA
  identifier, phone number identifier, display phone number, template namespace, and secure token
  placeholders.
- **FR-012**: System MUST enforce uniqueness for WhatsApp display phone number and WhatsApp phone
  number identifier across clinics.
- **FR-013**: System MUST create the first Clinic Admin account as part of onboarding and associate
  it only with the newly created clinic.
- **FR-014**: System MUST enforce uniqueness of the first Clinic Admin email across retained user
  accounts so one login email cannot silently belong to multiple accounts.
- **FR-015**: System MUST assign the first Clinic Admin the default clinic-admin role and default
  permissions needed to manage the clinic in later staff and settings features.
- **FR-016**: System MUST generate a temporary password for the first Clinic Admin, mark it for
  forced reset at first login, and prevent normal clinic access until reset is completed.
- **FR-017**: System MUST display or return the temporary password only once after successful
  onboarding and MUST NOT store, log, audit, or re-display it in recoverable plain text.
- **FR-018**: System MUST commit clinic profile, settings, reminder defaults, FAQ defaults, public
  slug reservation, WhatsApp metadata, first Clinic Admin, default permissions, temporary credential
  state, and required success audit events as one all-or-nothing onboarding operation.
- **FR-019**: System MUST roll back the entire onboarding operation when any required onboarding
  validation, uniqueness check, credential action, or audit write fails.
- **FR-020**: System MUST return field-specific, safe conflict feedback for duplicate clinic slug,
  first Clinic Admin email, WhatsApp display phone number, and WhatsApp phone number identifier.
- **FR-021**: System MUST write audit events for bootstrap availability, successful and failed
  onboarding attempts, clinic creation, first Clinic Admin creation, default permission assignment,
  WhatsApp metadata capture, public slug reservation, temporary credential issuance, and clinic
  deactivation.
- **FR-022**: System MUST keep audit metadata free of PHI, credentials, tokens, raw phone secrets,
  temporary passwords, and sensitive WhatsApp credentials.
- **FR-023**: System MUST deactivate a clinic only through a SUPER_ADMIN action with a required
  reason.
- **FR-024**: System MUST revoke refresh sessions for all users associated with a clinic when that
  clinic is deactivated.
- **FR-025**: System MUST reject session refresh for users whose clinic has been deactivated, even
  when their previous refresh session was issued before deactivation.
- **FR-026**: System MUST exclude full platform dashboard, appointment booking, public website
  pages, real WhatsApp template registration, and onboarding UI from this feature.

### Key Entities *(include if feature involves data)*

- **SUPER_ADMIN User**: A platform operator account with cross-tenant authority for onboarding and
  deactivation actions. SUPER_ADMIN permissions are role-derived rather than editable clinic
  permission rows.
- **Clinic**: A tenant boundary with profile, address, contact, timezone, default language, slug,
  active status, and operational defaults.
- **Clinic Settings**: The clinic's default operating configuration, including slot duration,
  booking windows, cancellation and reschedule cutoffs, language, and settings later clinic
  features will refine.
- **Operating Hours**: Clinic-local weekly availability windows established at onboarding for
  later scheduling features.
- **Reminder Defaults**: Default appointment reminder timing rules for later notification records
  and scheduler behavior.
- **FAQ Default**: Seed clinic FAQ content created during onboarding so future WhatsApp and clinic
  customization features have an initial knowledge base.
- **Public Website Slug Reservation**: A unique public-facing slug and enablement flag reserved for
  later public clinic website features.
- **WhatsApp Metadata**: Per-clinic business account and phone metadata, including WABA identifier,
  display number, phone number identifier, template namespace, and secure credential placeholders.
- **Clinic Admin User**: The first tenant-scoped administrator created during onboarding, assigned
  to the clinic and required to reset a temporary password on first login.
- **Default Permission Set**: Initial role-derived and module-level permissions assigned to the
  first Clinic Admin for later clinic operations.
- **Temporary Credential State**: One-time password issuance state that requires first-login reset
  and is not recoverable after display.
- **Refresh Session**: A revocable authenticated session that must be invalidated for users of a
  deactivated clinic.
- **Audit Event**: A PHI-redacted record of bootstrap, onboarding, uniqueness failure, credential,
  permission, WhatsApp metadata, public slug, and deactivation activity.

### Constitution Alignment *(mandatory)*

- **Tenant Isolation**: Clinic scope is created by SUPER_ADMIN onboarding and then becomes the
  trusted tenant boundary for future clinic users. Clinic users do not provide authoritative
  `clinicId`. SUPER_ADMIN onboarding and deactivation are cross-tenant platform actions and must be
  explicitly audited with actor and target clinic.
- **Appointment/Channel Lifecycle**: Appointment booking and message dispatch are excluded. This
  feature prepares clinic defaults, reminder defaults, operating hours, and WhatsApp metadata that
  later appointment and channel features will consume without creating appointments or registering
  live templates.
- **PHI/Security/Audit**: The feature handles platform identity, staff credentials, clinic contact
  details, phone metadata, WhatsApp credential placeholders, temporary passwords, refresh-session
  revocation, and deactivation reasons. It requires PHI-safe responses, no temporary-password or
  token logging, protected credential display, same-operation audit writes, and SUPER_ADMIN-only
  access.
- **Module Boundaries**: Affects platform administration, clinic onboarding, authentication,
  permissions bootstrap, clinic settings defaults, WhatsApp metadata ownership, public website slug
  reservation, refresh-session revocation, and audit. Later modules consume the created defaults
  through their own service boundaries.
- **Required Tests**: Requires bootstrap idempotency tests, SUPER_ADMIN-only authorization tests,
  onboarding transaction rollback tests, uniqueness conflict tests, temporary-password one-time
  display and forced-reset tests, PHI-safe logging/audit tests, clinic deactivation token
  revocation tests, and concurrency tests for duplicate slug, admin email, WhatsApp display number,
  and phone number identifier.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In a fresh setup, 100% of validation runs produce exactly one active bootstrap
  SUPER_ADMIN capable of signing in, with no duplicate bootstrap account after repeated setup.
- **SC-002**: 100% of non-SUPER_ADMIN attempts to create clinics or deactivate clinics are rejected
  before any clinic, user, settings, WhatsApp, permission, credential, refresh-session, or success
  audit state changes.
- **SC-003**: 100% of successful onboarding attempts create the clinic profile, settings, reminder
  defaults, FAQ defaults, public slug reservation, WhatsApp metadata, first Clinic Admin, default
  permissions, temporary credential state, and audit records together.
- **SC-004**: 100% of forced onboarding failures leave zero partial clinic onboarding records and
  zero misleading success audit events.
- **SC-005**: 100% of duplicate slug, first-admin email, WhatsApp display number, and WhatsApp
  phone number identifier attempts return field-specific conflict feedback and create no new clinic.
- **SC-006**: 100% of first Clinic Admin accounts created by onboarding must reset their temporary
  password before normal clinic access.
- **SC-007**: 0 temporary passwords, credential tokens, WhatsApp secrets, or raw sensitive values
  appear in application logs, audit metadata, or repeatable responses during onboarding tests.
- **SC-008**: 100% of clinic deactivation operations revoke refresh sessions for users of the
  target clinic, and 100% of those users are unable to refresh sessions afterward.

## Assumptions

- The latest user prompt intentionally excludes onboarding UI even though `feature.md` mentions a
  minimal SUPER_ADMIN onboarding UI; this specification keeps UI out of F02.
- The bootstrap SUPER_ADMIN identity and initial secret source will be defined during planning and
  must avoid committing live credentials to the repository.
- Clinic Admin email is globally unique across retained user accounts for MVP simplicity and to
  avoid ambiguous login ownership.
- Public website slug reservation is created now so later public website features can resolve a
  clinic by slug, but no public page or booking flow is exposed in this feature.
- WhatsApp metadata is stored and validated now, while real template registration and live message
  sending are deferred to later WhatsApp features.
- Default FAQ seed content is platform-approved generic clinic content and can be customized by
  later clinic settings or FAQ features.
- Clinic deactivation is reversible only if a later support or restoration feature defines that
  flow; F02 only requires safe inactive state and refresh-session revocation.
