# Cliniqo AI Feature Execution Plan

This file is the working feature source for Spec Kit. It replaces the need to
keep `PRODUCT_REQUIREMENTS.md` and `ARCHITECTURE.md` updated during execution.

Use one feature at a time:

```text
$speckit-constitution
$speckit-specify <copy one feature prompt from this file>
$speckit-plan
$speckit-tasks
$speckit-implement
```

## Product Summary

Cliniqo AI is a multi-tenant clinic automation platform for small clinics. It
supports appointment creation from WhatsApp AI, receptionist/manual booking,
clinic admin booking, super admin support booking, and a per-clinic public
booking website. Every appointment enters the same lifecycle: confirmation,
appointment reminders, post-appointment follow-up, optional medicine reminders,
dashboard visibility, audit, and analytics.

Patients do not install an app. Their main channel is WhatsApp. Clinic staff use
a React dashboard. The backend is a Spring Boot modular monolith with
PostgreSQL, Flyway, JWT auth, strict tenant isolation, audit logging, and
scheduled jobs.

## Non-Negotiable Architecture Rules

- Use a modular monolith, not microservices.
- Use Spring Boot backend, PostgreSQL database, Flyway migrations, React,
  TypeScript, TailwindCSS, and Vite frontend.
- Keep modules separated by domain: auth, clinic, doctor, patient,
  appointment, whatsapp, ai, notification, analytics, public-website,
  medicine-reminder, super-admin, and audit.
- Controllers validate and delegate. Services own business logic. Repositories
  own queries. DTOs are API contracts and must not expose JPA entities.
- All business tables include `clinic_id`, except tenant-exempt tables such as
  `clinics`, `refresh_tokens`, `flyway_schema_history`, and static lookup data.
- Clinic users must never supply `clinicId` in request bodies. Clinic scope comes
  from JWT-derived `TenantContext`. Public website scope comes from slug-derived
  `WebsiteContext`. Super admin scope comes only from explicit
  `/api/v1/super-admin/clinics/{clinicId}/...` paths.
- Cross-tenant access by clinic users returns 404, not 403, to avoid existence
  leaks.
- SUPER_ADMIN is the only cross-tenant role and every cross-tenant write is
  audited with target clinic and actor.
- Patient identity is `(clinic_id, phone)`. The same phone may exist in multiple
  clinics as separate patients.
- Appointment writes must be ACID: slot lock, appointment write, audit event, and
  notification record are committed or rolled back together.
- AI is stateless. It can classify intent, extract entities, detect language,
  and generate replies. It cannot mutate data, bypass validation, or make
  authorization decisions.
- WhatsApp is per clinic. Each clinic has its own WABA credentials and
  `phone_number_id`. Inbound tenant resolution uses `phone_number_id`.
- PHI, secrets, patient message bodies, phone numbers, and medicine names must
  not be logged in plain text.
- Use soft delete for business records. Purge is SUPER_ADMIN-only and audited.

## Feature Sequence

| Order | ID | Feature | Depends On | Result |
| --- | --- | --- | --- | --- |
| 1 | F01 | Foundation, Tenancy, Auth, Audit | None | Secure modular backend shell |
| 2 | F02 | Super Admin Bootstrap and Clinic Onboarding | F01 | First clinics can be created |
| 3 | F03 | Clinic Users, Roles, Permissions | F01, F02 | Staff access is permissioned |
| 4 | F04 | Clinic Settings, Doctors, Schedules, Availability | F01-F03 | Bookable slots exist |
| 5 | F05 | Patient Records and Cross-Clinic Identity | F01-F03 | Patients are tenant-safe |
| 6 | F06 | Core Appointment Lifecycle | F01-F05 | Shared booking engine exists |
| 7 | F07 | Notification Records and Reminder Scheduler | F06 | Confirmation/reminder pipeline exists |
| 8 | F08 | Receptionist Dashboard and Manual Booking | F03, F06, F07 | Staff can run daily booking |
| 9 | F09 | WhatsApp Webhook and Messaging Infrastructure | F02, F05-F07 | WhatsApp is tenant-safe |
| 10 | F10 | AI Intent, Language, and FAQ Automation | F09 | Messages can be understood |
| 11 | F11 | WhatsApp Appointment Booking | F06, F09, F10 | Patients can book in chat |
| 12 | F12 | WhatsApp Reschedule, Cancel, Lookup, Handoff | F06, F08-F11 | Patients can self-serve changes |
| 13 | F13 | Post-Appointment Follow-Up and Auto Status | F07, F09, F12 | Status can be auto-marked |
| 14 | F14 | Public Clinic Booking Website | F02, F04-F07, F09 | Public slug booking works |
| 15 | F15 | Medicine Reminders | F05, F07, F09, F13 | Patients can opt into dose reminders |
| 16 | F16 | Clinic Staff Dashboard and Analytics | F03, F06-F08, F13 | Clinic operations are visible |
| 17 | F17 | Super Admin Support, Purge, Platform Audit | F02, F03, F06, F16 | Platform ops can support safely |
| 18 | F18 | Observability, Health, Backup, Runbooks | F01-F17 | MVP is production-operable |

## F01 - Foundation, Tenancy, Auth, Audit

Goal: Create the backend foundation every later feature depends on.

Scope:

- Spring Boot modular monolith package structure.
- PostgreSQL and Flyway baseline migrations.
- UUID primary keys, audit columns, `deleted_at`, `deleted_by`, and soft-delete
  base entity.
- JWT authentication with access/refresh tokens.
- `TenantContext` from JWT for clinic users.
- `WebsiteContext` placeholder for future public booking.
- SUPER_ADMIN route context placeholder for explicit clinic path operations.
- Global API response envelope and typed exception hierarchy.
- Request ID propagation and structured PHI-safe logging.
- Central audit module with write-mostly `audit_events`.
- Test fixtures for two clinics and cross-tenant leak tests.
- Architecture checks that reject controller DTOs containing `clinicId` and
  tenant-blind repository patterns where feasible.

Out of scope:

- Clinic onboarding UI.
- Appointment booking.
- WhatsApp or OpenAI integration.
- Analytics screens.

Acceptance checks:

- Clinic-scoped endpoint without tenant context is rejected.
- Clinic user from Clinic A cannot read, list, update, or infer Clinic B records.
- Request body `clinicId` is ignored and logged as tenancy tampering.
- Audit events commit in the same transaction as the action.
- Global error responses never leak stack traces or PHI.

Speckit prompt:

```text
$speckit-specify Implement F01 Foundation, Tenancy, Auth, Audit for Cliniqo AI. Build a Spring Boot modular monolith foundation with PostgreSQL, Flyway, UUID IDs, soft-delete base entity, audit columns, JWT auth, refresh tokens, TenantContext from JWT, WebsiteContext placeholder, explicit SUPER_ADMIN clinic route context placeholder, global API envelope, typed exceptions, request ID propagation, PHI-safe structured logging, and a centralized audit module. Enforce that clinic users never supply clinicId from request bodies, cross-tenant access returns 404, audit writes happen in the same transaction as the parent action, and integration tests cover Clinic A attempting to access Clinic B data. Exclude clinic onboarding UI, appointments, WhatsApp, OpenAI, and analytics screens.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F02 - Super Admin Bootstrap and Clinic Onboarding

Goal: Let platform operators create the first clinics safely.

Scope:

- Flyway seed for initial SUPER_ADMIN bootstrap user.
- SUPER_ADMIN login and SUPER_ADMIN-only route protection.
- Atomic clinic onboarding transaction.
- Clinic fields: name, slug, address, country, timezone, default language,
  operating hours, primary phone, email, slot duration, booking windows,
  cancellation/reschedule cutoffs, logo metadata.
- WhatsApp metadata: WABA ID, phone number ID, display number, template
  namespace, encrypted token placeholders.
- Public website slug reservation and enable flag.
- First Clinic Admin creation with temporary password, forced first-login reset,
  and one-time credential display.
- Default clinic settings, reminder settings, FAQ seeds, and permissions.
- Audit of every onboarding action.
- Deactivating clinic revokes clinic refresh tokens.

Out of scope:

- Full super admin dashboard.
- Appointment booking.
- Public website pages.
- Actual WhatsApp template registration automation.

Acceptance checks:

- Clinic and first admin are created atomically or rolled back together.
- Duplicate slug, admin email, or WhatsApp number fails cleanly.
- Initial SUPER_ADMIN exists after migration.
- Clinic Admin cannot create SUPER_ADMIN users.
- Temporary passwords are never logged and are shown once.

Speckit prompt:

```text
$speckit-specify Implement F02 Super Admin Bootstrap and Clinic Onboarding for Cliniqo AI. Seed the first SUPER_ADMIN with Flyway, protect SUPER_ADMIN-only APIs, and create an atomic onboarding flow that creates clinic profile, unique slug, timezone, operating hours, default language, settings, reminder defaults, FAQ defaults, public website slug, WhatsApp metadata, first Clinic Admin, default permissions, temporary password with forced first-login reset, and audit events. Enforce unique slug, clinic admin email, WABA phone number, and phone_number_id. Deactivating a clinic must revoke its users' refresh tokens. Exclude full platform dashboard, appointment booking, public website pages, and real WhatsApp template registration.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F03 - Clinic Users, Roles, Permissions

Goal: Let clinic admins manage staff with granular permissions.

Scope:

- Roles: CLINIC_ADMIN, RECEPTIONIST, DOCTOR.
- User invite/create, activate, suspend, soft delete, restore.
- Password reset on first login for system-issued passwords.
- Per-module permissions: none, read, read-write, admin.
- Modules: appointments, patients, doctors, conversations, faqs, analytics,
  clinic_settings, users, billing.
- Server-driven navigation/permissions endpoint for frontend.
- Permission aspect or equivalent server-side enforcement.
- Doctor user linkage to one doctor profile.
- Last active CLINIC_ADMIN protection.
- Audit before/after permission changes.

Out of scope:

- SUPER_ADMIN two-person management.
- Appointment pages.
- Analytics implementation.

Acceptance checks:

- Clinic Admin cannot create users outside their clinic.
- Clinic Admin cannot create SUPER_ADMIN users.
- RECEPTIONIST cannot receive admin on users, billing, or clinic settings.
- Revoked permission affects the next API call.
- DOCTOR scope is limited to own doctor records where applicable.

Speckit prompt:

```text
$speckit-specify Implement F03 Clinic Users, Roles, Permissions for Cliniqo AI. Clinic Admins must create and manage Clinic Admin, Receptionist, and Doctor users only within their own clinic, assign per-module permission levels none/read/read-write/admin, enforce first-login password reset, expose server-driven navigation permissions, audit permission and user changes with before/after values, protect the last active Clinic Admin, and link Doctor users to doctor profiles. Server-side authorization must reject insufficient permissions with 403 and cross-tenant access with 404. Exclude SUPER_ADMIN two-person management, appointment operations, and analytics implementation.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F04 - Clinic Settings, Doctors, Schedules, Availability

Goal: Create bookable clinic and doctor availability.

Scope:

- Clinic settings for timezone, languages, operating hours, slot duration,
  booking advance window, booking notice, cancellation cutoff, reschedule cutoff,
  reminder windows, follow-up windows, website enable flag.
- Slot duration allowed values: 15, 20, 30, 45, 60 minutes.
- Booking max advance default 30 days, allowed 7-90 days.
- Booking notice default 60 minutes, allowed 15-1440 minutes.
- Cancellation cutoff default 120 minutes, allowed 0-1440 minutes.
- Doctor CRUD, active/inactive state, profile fields, speciality, languages.
- Doctor schedules and working hours.
- Slot availability query reused by WhatsApp, receptionist, clinic admin, public
  website, and super admin flows.
- Clinic timezone handling for all date boundaries.

Out of scope:

- Appointment creation.
- Multi-branch clinics.
- Per-doctor slot duration override.

Acceptance checks:

- Availability returns only clinic-scoped active doctors.
- Past dates and too-far-ahead dates are rejected.
- Invalid setting ranges return typed 422 errors.
- Cross-tenant doctor/schedule access returns 404.
- Timezone tests cover clinic-local date behavior.

Speckit prompt:

```text
$speckit-specify Implement F04 Clinic Settings, Doctors, Schedules, Availability for Cliniqo AI. Authorized clinic users must manage clinic settings, doctors, doctor schedules, working hours, slot duration allowed values, booking max advance window, booking notice, cancellation cutoff, reschedule cutoff, reminder windows, follow-up windows, timezone, languages, and website enable flag. Provide a reusable slot availability service for WhatsApp, receptionist, clinic admin, public website, and SUPER_ADMIN flows. Enforce tenant isolation, typed validation errors, clinic-local timezone behavior, and cross-tenant tests. Exclude appointment creation, public website pages, and WhatsApp/OpenAI integration.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F05 - Patient Records and Cross-Clinic Identity

Goal: Implement tenant-safe patient records.

Scope:

- Patient CRUD/search for authorized clinic staff.
- Unique patient identity by `(clinic_id, phone)`.
- Same phone allowed in multiple clinics as separate patients.
- Fields: name, phone, language preference, opt-out flags, WhatsApp reachability,
  audit fields, soft delete fields.
- Find-or-create/upsert by phone for appointment channels.
- Soft delete and restore for patients.
- Patient appointment history scoped to clinic.
- PHI-safe search and logs.

Out of scope:

- Patient login.
- Patient self-deletion.
- Medicine reminder courses.

Acceptance checks:

- Same phone can be used in Clinic A and Clinic B independently.
- Updating/deleting Clinic A patient does not affect Clinic B patient.
- Search by name/phone is clinic-scoped.
- Auto-registration is concurrency-safe.
- Soft-deleted patient is invisible to normal reads and restorable when eligible.

Speckit prompt:

```text
$speckit-specify Implement F05 Patient Records and Cross-Clinic Identity for Cliniqo AI. Patients must be scoped by clinic and uniquely identified by `(clinic_id, phone)`, allowing the same phone number in multiple clinics as independent patient records. Authorized clinic users must search and manage patients within their clinic, appointment channels must find-or-create patients by phone, language preference and reminder opt-out fields must be stored per clinic, patient history must remain tenant-scoped, soft delete/restore must preserve audit integrity, and PHI must not appear in logs. Exclude patient login, patient self-deletion, appointment booking, and medicine reminder courses.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F06 - Core Appointment Lifecycle

Goal: Build the central appointment engine shared by every channel.

Scope:

- Appointment entity with clinic, doctor, patient, scheduled time, duration,
  status, createdBy, notes, cancellation reason, reschedule linkage, auto-mark
  fields, and audit fields.
- Statuses: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW, RESCHEDULED.
- Created-by values: whatsapp-ai, manual-receptionist, clinic-website,
  clinic-admin, super-admin.
- Booking service with doctor availability validation and patient linkage.
- DB-level double-booking protection on active doctor/time slots.
- Row locking or equivalent concurrency control.
- Reschedule service with W1-W5 windows.
- Cancellation service with configurable cutoff.
- Status update service with allowed transitions.
- Immutable `createdBy`; reschedule preserves original channel on new row.
- Idempotency support for mutating operations.
- Audit and notification-record creation inside the transaction.

Out of scope:

- WhatsApp chat flows.
- Receptionist UI.
- Public website UI.
- Actual notification dispatch.

Acceptance checks:

- Concurrent booking attempts cannot double-book a slot.
- Booking, reschedule, cancel, audit, and notification record are atomic.
- Cross-tenant appointment reads and writes return 404.
- Invalid state transitions fail with typed errors.
- W4 after-appointment reschedule and W5 blocked behavior are tested.

Speckit prompt:

```text
$speckit-specify Implement F06 Core Appointment Lifecycle for Cliniqo AI. Create the shared appointment engine used by WhatsApp AI, receptionist, public website, clinic admin, and super admin channels. Include appointment status lifecycle, immutable createdBy values, patient/doctor/clinic links, booking, reschedule, cancel, status update services, DB-level double-booking prevention, row locking or equivalent concurrency control, W1-W5 reschedule windows, cancellation cutoff validation, idempotency for mutating requests, transactional audit events, and transactional notification record creation. Exclude WhatsApp conversation handling, receptionist UI, public website UI, and actual notification dispatch.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F07 - Notification Records and Reminder Scheduler

Goal: Persist confirmation and reminder work before sending messages.

Scope:

- Notifications table for APPOINTMENT_CONFIRMATION, APPT_REMINDER,
  FOLLOW_UP_PROMPT, AUTO_STATUS, and future MEDICINE_REMINDER.
- Statuses: PENDING, SENT, DELIVERED, READ, FAILED, HANDLED.
- Scheduled time, sent time, retry count, last error, failure category, related
  appointment or dose ID.
- No PHI-heavy message body stored.
- Post-commit dispatcher pattern.
- Morning-of reminder at clinic-local configurable time, default 08:00.
- Pre-appointment reminder, default 2 hours before.
- Fixed retry policy: 5 attempts total with 1m, 5m, 15m, 60m, 4h backoff.
- Failed reminder visibility data for receptionist dashboard.
- Patient STOP REMINDERS opt-out per clinic.
- Reminder confirmation reply support marks appointment confirmed internally
  while status remains SCHEDULED.

Out of scope:

- Medicine reminder course creation.
- Post-appointment auto-status decisions.
- Full WhatsApp adapter if not present; use an interface/mock where needed.

Acceptance checks:

- Reminders send only for SCHEDULED appointments.
- Cancelled/completed appointments are skipped.
- Failed sends retry and become FAILED after final attempt.
- Notification records do not store patient message bodies or medicine names.
- Clinic timezone controls schedule windows.

Speckit prompt:

```text
$speckit-specify Implement F07 Notification Records and Reminder Scheduler for Cliniqo AI. Persist notification records for appointment confirmations, appointment reminders, follow-up prompts, auto-status records, and future medicine reminders. Use a post-commit dispatcher, clinic-timezone scheduling, morning-of and pre-appointment reminders, fixed retry policy of 5 attempts total with configured backoff, sent/delivered/read/failed states, failure categories, receptionist failure visibility, and patient STOP REMINDERS opt-out per clinic. Notification records and logs must avoid PHI-heavy message bodies. Exclude medicine reminder course creation and post-appointment auto-status decisions.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F08 - Receptionist Dashboard and Manual Booking

Goal: Give receptionists a usable daily operations workflow.

Scope:

- Today's appointments sorted by time.
- Appointment filters by date, doctor, status, source, patient name, phone.
- Patient search and patient detail with upcoming and past appointments.
- New appointment form using the shared slot availability and booking service.
- Manual booking creates appointment with `createdBy = manual-receptionist`.
- Auto-register or link patient by phone.
- Reschedule, cancel, edit notes, and status quick actions.
- Reminder failures list with Retry and Mark Handled.
- Delivery status indicator for WhatsApp confirmation.
- Near-real-time refresh for new bookings where feasible.
- Audit every receptionist action.

Out of scope:

- Full analytics dashboard.
- Drag-to-reschedule calendar.
- AI handoff reply UI if conversations are not implemented yet.

Acceptance checks:

- Dashboard loads clinic-scoped data only.
- Manual booking uses same double-booking rules as all channels.
- WhatsApp delivery failure does not roll back saved appointment.
- Receptionist permissions are enforced server-side.
- All modifications are audited.

Speckit prompt:

```text
$speckit-specify Implement F08 Receptionist Dashboard and Manual Booking for Cliniqo AI. Receptionists must view today's appointments sorted by time, filter appointments by date/doctor/status/source/search, search patients, open patient detail, manually create appointments with `createdBy = manual-receptionist`, auto-register or link patients by phone, reschedule/cancel/edit notes/update status, see WhatsApp confirmation delivery status, and manage failed reminders with Retry or Mark Handled. Reuse shared appointment, availability, patient, notification, audit, tenancy, and permission services. Exclude full analytics, drag-to-reschedule future calendar behavior, and AI conversation reply UI unless already available.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F09 - WhatsApp Webhook and Messaging Infrastructure

Goal: Make WhatsApp safe and tenant-aware before adding AI flows.

Scope:

- `clinic_whatsapp_config` per clinic with encrypted access token, webhook verify
  token, app secret, WABA ID, phone number ID, display number, template namespace,
  status, rotation timestamp.
- Inbound webhook endpoint.
- Tenant resolution from webhook `metadata.phone_number_id`.
- Per-clinic signature verification using stored app secret.
- Unknown phone number ID returns 200 to Meta, drops processing, and audits.
- Invalid signature returns 401 and audits.
- Conversation and message persistence by clinic.
- Idempotency by WhatsApp message ID.
- Outbound send adapter that uses the resolved clinic credentials.
- Delivery/read status callback routing.
- Template vs session-message distinction.
- Failure categories for auth token, template missing, recipient invalid, rate
  limit, network.

Out of scope:

- AI intent classification.
- Chat booking flows.
- Receptionist conversation UI.

Acceptance checks:

- Clinic A WABA message cannot persist under Clinic B.
- Outbound sends refuse missing tenant context.
- Secrets are encrypted at rest and never logged.
- Duplicate inbound message IDs are idempotent.
- Failure status belongs only to the owning clinic.

Speckit prompt:

```text
$speckit-specify Implement F09 WhatsApp Webhook and Messaging Infrastructure for Cliniqo AI. Each clinic must store encrypted WhatsApp Business credentials including WABA ID, phone_number_id, display number, access token, webhook verify token, app secret, template namespace, and status. Inbound webhooks must resolve `phone_number_id` to clinic, verify signatures using the resolved clinic app secret, set trusted tenant context, persist conversations/messages under that clinic, deduplicate by WhatsApp message ID, and safely handle unknown tenants and invalid signatures. Outbound sending must use only the owning clinic credentials and route delivery/read callbacks by the same tenant key. Exclude AI intent classification, chat-based appointment booking, and receptionist conversation UI.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F10 - AI Intent, Language, and FAQ Automation

Goal: Understand patient messages without letting AI own business logic.

Scope:

- OpenAI adapter with 8-second hard timeout.
- Retry/circuit breaker fallback behavior.
- Externalized prompts under docs/prompts or equivalent.
- Structured AI response: intent, confidence, entities, reply, language.
- Supported languages: English, Hindi, Kannada.
- Supported intents: book, reschedule, cancel, check availability, lookup
  appointment, FAQ timings, FAQ location, FAQ services, FAQ fees, FAQ doctors,
  FAQ general, talk to human, medicine list/pause/resume/stop/update, done/skip.
- FAQ records per clinic stored in DB.
- FAQ matching uses clinic-scoped data only.
- Low confidence, timeout, or sensitive/unhandled message marks conversation for
  receptionist handoff.
- Sanitized patient-facing replies.

Out of scope:

- Direct appointment mutation.
- Clinical advice.
- Prescription validation.

Acceptance checks:

- AI has no repository access and no write authority.
- Every AI-suggested action goes through deterministic services.
- FAQ answers are clinic-specific and language-aware.
- Failed AI call produces fallback and handoff marker.
- Prompts and logs do not dump PHI.

Speckit prompt:

```text
$speckit-specify Implement F10 AI Intent, Language, and FAQ Automation for Cliniqo AI. Build a stateless AI module that calls OpenAI with timeout, retry, circuit breaker, externalized prompts, structured JSON output, intent classification, entity extraction, language detection for English/Hindi/Kannada, clinic-scoped FAQ matching, and localized reply generation. Low confidence, timeout, sensitive topics, or repeated failure must mark the conversation for receptionist handoff. AI must never mutate data, access repositories directly, bypass appointment validation, or make authorization decisions. Exclude appointment execution, clinical advice, prescription validation, and receptionist reply UI.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F11 - WhatsApp Appointment Booking

Goal: Let patients book appointments through natural WhatsApp chat.

Scope:

- BOOK_APPOINTMENT flow.
- Patient identity from sender phone plus resolved clinic.
- Patient find-or-create.
- AI extraction of doctor, date, time, language.
- Missing doctor/date/time prompts.
- Show doctors if no doctor specified.
- Show available slots for selected doctor/date.
- Explicit patient slot selection before booking.
- Appointment created as SCHEDULED with `createdBy = whatsapp-ai`.
- Confirmation includes doctor name, date, time, clinic address.
- Audit and notification records in the appointment transaction.
- Conversation state for multi-turn booking.

Out of scope:

- Reschedule/cancel/lookup.
- Public website booking.
- Medicine reminders.

Acceptance checks:

- Patient can book with doctor/date/time.
- Missing data produces clear follow-up prompts.
- AI output is validated by appointment service.
- Concurrent attempts cannot double-book.
- Confirmation notification is persisted before outbound sending.

Speckit prompt:

```text
$speckit-specify Implement F11 WhatsApp Appointment Booking for Cliniqo AI. Patients messaging a clinic WhatsApp number must be identified by sender phone and resolved clinic, AI must classify BOOK_APPOINTMENT and extract doctor/date/time/language, the bot must ask for missing details, show available doctors or slots, require explicit patient slot selection, find-or-create the patient, create a SCHEDULED appointment with `createdBy = whatsapp-ai`, persist audit and notification records transactionally, prevent double booking, and send a confirmation with doctor, date, time, and clinic address. Exclude reschedule, cancellation, lookup, public website booking, and medicine reminders.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F12 - WhatsApp Reschedule, Cancel, Lookup, Handoff

Goal: Complete core WhatsApp self-service after booking.

Scope:

- RESCHEDULE_APPOINTMENT using phone plus clinic lookup.
- W1-W5 reschedule windows.
- Multiple appointment disambiguation.
- Old appointment marked RESCHEDULED or NO_SHOW as rules require; new
  appointment created and linked.
- CANCEL_APPOINTMENT with reschedule-first prompt.
- Cancellation requires explicit confirmation and optional reason.
- LOOKUP_APPOINTMENT rate-limited per patient phone.
- Lookup shows upcoming SCHEDULED appointments; if none, offers booking.
- TALK_TO_HUMAN and AI fallback handoff.
- Handoff stores conversation history and ESCALATED state.
- Handoff can later return to AI.

Out of scope:

- Initial booking if F11 is complete.
- Post-appointment follow-up.
- Full receptionist chat UI.

Acceptance checks:

- Cancellation cannot happen before reschedule-first prompt and explicit confirm.
- Reschedule follows W1-W5 behavior.
- Lookup exposes only the sender's clinic-scoped appointments.
- Handoff events are visible to receptionist workflows.
- All changes create audit and notification records.

Speckit prompt:

```text
$speckit-specify Implement F12 WhatsApp Reschedule, Cancel, Lookup, Handoff for Cliniqo AI. Patients must reschedule appointments using W1-W5 rules, disambiguate multiple upcoming appointments, cancel only after the bot offers reschedule first and receives explicit confirmation, optionally provide cancellation reason, look up their own appointments by sender phone and resolved clinic, and request or receive receptionist handoff when AI confidence is low, repeated attempts fail, sensitive topics appear, or the patient asks for a human. All flows must use deterministic appointment services, tenant-safe conversation state, rate limits for lookup, audit events, and notification records. Exclude initial booking, public website booking, post-appointment auto-status, and full receptionist chat UI.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F13 - Post-Appointment Follow-Up and Auto Status

Goal: Reduce staff work by asking patients if they attended.

Scope:

- Follow-up scheduler after appointment time plus clinic setting, default 3
  hours.
- WhatsApp prompt: attended, did not attend, reschedule.
- Attended sets COMPLETED with auto-marked verification badge.
- Did not attend sets NO_SHOW with auto-marked verification badge.
- Reschedule enters existing reschedule flow.
- No response after fallback window, default 24 hours, sets NO_SHOW with badge.
- Auto-status never overwrites staff-set status.
- Override endpoint for receptionist, doctor where allowed, or clinic admin.
- Override clears verification badge and writes audit.
- Needs-verification query for dashboard.

Out of scope:

- Medicine reminder course management.
- Full analytics UI.

Acceptance checks:

- Cancelled appointments receive no follow-up.
- Manually changed statuses are not overwritten.
- Fallback job fires within scheduler tolerance.
- Override writes actor, timestamp, old value, new value.
- Needs-verification badge/filter works.

Speckit prompt:

```text
$speckit-specify Implement F13 Post-Appointment Follow-Up and Auto Status for Cliniqo AI. After appointment time plus clinic-configured offset, send a WhatsApp follow-up asking whether the patient attended. Process Attended, Did not attend, and Reschedule replies; auto-mark COMPLETED or NO_SHOW with an auto-marked please-verify badge; trigger fallback NO_SHOW after the configured no-response window; never overwrite manually set statuses; skip cancelled appointments; allow authorized staff overrides that clear the badge and write audit; and expose needs-verification data for dashboards. Exclude medicine reminder course management and full analytics UI.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F14 - Public Clinic Booking Website

Goal: Let unauthenticated patients book through a clinic-specific URL.

Scope:

- Public route by slug, such as `https://{slug}.cliniqo.app`.
- Slug resolver sets `WebsiteContext`.
- Friendly disabled website page.
- Public clinic profile, address, hours, doctors, doctor detail, available slots.
- Mobile-first responsive pages.
- SEO metadata with clinic name, address, doctors.
- Booking form with name, E.164 phone, selected slot.
- CAPTCHA or honeypot.
- Booking intent state machine: DRAFT, AWAITING_OTP, VERIFIED, CONFIRMED,
  EXPIRED, LOCKED.
- OTP via WhatsApp, 5-minute TTL, 60-second resend cooldown, max 3 resends,
  max 5 verification attempts.
- Rate limits: per phone 5 send-otp/hour, per IP 20 send-otp/hour,
  per IP 100 verify-otp/hour, per slug 100 booking intents/hour.
- Confirm booking only after OTP verification.
- Appointment created with `createdBy = clinic-website`.
- Privacy-safe confirmation page and WhatsApp confirmation.

Out of scope:

- Cross-clinic marketplace.
- Patient login/portal.
- Online payments.
- Reviews/ratings.

Acceptance checks:

- Public APIs never accept or trust `clinicId`.
- Public pages expose no patient data.
- OTP is required before final booking.
- Slot is not double-booked under race conditions.
- Disabled clinic website returns friendly 200 page.

Speckit prompt:

```text
$speckit-specify Implement F14 Public Clinic Booking Website for Cliniqo AI. Each clinic must have a public slug URL resolved into WebsiteContext, public mobile-first pages for clinic profile, doctor list, doctor detail, and available slots, SEO metadata, a booking form with name/phone/slot, CAPTCHA or honeypot, booking intents, WhatsApp OTP with 5-minute TTL, 60-second resend cooldown, max 3 resends, max 5 verification attempts, per-phone/IP/slug rate limits, and final confirmation that creates an appointment with `createdBy = clinic-website` only after OTP verification. Public routes must never trust clinicId from input and must not expose PHI. Exclude cross-clinic marketplace, patient login, payments, reviews, and ratings.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F15 - Medicine Reminders

Goal: Offer voluntary patient-entered medicine reminders after completed visits.

Scope:

- Opt-in prompt after COMPLETED appointment.
- Disclaimer that this is not clinical prescription management.
- Patient-defined medicine course fields: medicine name, dosage text, notes,
  dose times, duration/start/end date, status.
- Dose schedule with reminder lead minutes default 10, allowed 0-60.
- WhatsApp controls: list, pause, resume, stop, update.
- DONE/SKIP adherence replies.
- Unknown/no reply remains UNKNOWN without nag loop.
- End-of-course follow-up offering follow-up appointment.
- Encryption/redaction for medicine names and dosages.
- Logs reference course IDs only, not medicine names.

Out of scope:

- Doctor-entered prescriptions.
- Drug interaction checks.
- Dosage validation.
- Pharmacy integration.
- Clinical claims.

Acceptance checks:

- No medicine reminder is created without explicit opt-in.
- Patient can pause/resume/stop/update through WhatsApp.
- Reminders fire within tolerance.
- DONE/SKIP updates adherence.
- Medicine names and dosages are never logged in plain text.

Speckit prompt:

```text
$speckit-specify Implement F15 Medicine Reminders for Cliniqo AI. After a completed visit, patients may explicitly opt into voluntary medicine reminders through WhatsApp with a clear non-clinical disclaimer. Store patient-defined medicine courses with medicine name, dosage text, notes, dose times, start/end or duration, status, and reminder lead minutes default 10 within the 0-60 range. Patients must list, pause, resume, stop, update, and reply DONE or SKIP; adherence must be tracked; no-reply must remain UNKNOWN without nagging; end-of-course follow-up must offer a next appointment; and medicine names/dosages must be encrypted or redacted and never logged. Exclude doctor prescriptions, drug interaction checks, dosage validation, pharmacy integrations, and clinical claims.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F16 - Clinic Staff Dashboard and Analytics

Goal: Give clinic staff role-aware visibility into operations.

Scope:

- Server-driven navigation based on permissions.
- Dashboard cards: today's appointments, upcoming next 7 days, no-shows this
  week, bookings by source, reminder confirmation rate, open escalated
  conversations.
- Appointment page with filters, status pills, source badges, quick actions.
- Status UI labels mapped from canonical statuses.
- Needs-verification widget for auto-marked appointments.
- Analytics by configurable date range: total appointments, missed/no-show rate,
  cancellation rate, returning vs new patients, appointments by doctor,
  appointments by source, peak booking hours, reminder effectiveness.
- Date buckets use clinic timezone.
- Non-SA analytics max range 90 days.
- Analytics permission enforcement server-side.

Out of scope:

- CSV export.
- Platform-level dashboard.
- Real-time analytics beyond operational refresh.

Acceptance checks:

- Dashboard shows only clinic-scoped data.
- Sidebar is server-permission-driven.
- Source metrics use immutable `createdBy`.
- Doctor view sees own schedule/patients only.
- Date ranges and buckets respect clinic timezone.

Speckit prompt:

```text
$speckit-specify Implement F16 Clinic Staff Dashboard and Analytics for Cliniqo AI. Clinic users must receive server-permission-driven navigation and dashboard cards for today's appointments, upcoming appointments, no-shows, bookings by source, reminder confirmation rate, and open escalated conversations. Appointment pages must support filters, status pills, source badges, quick actions, and needs-verification badges. Analytics-authorized users must view aggregates by date range, status, doctor, source, cancellation, no-show, retention, peak booking hours, and reminder effectiveness, using clinic timezone and immutable createdBy. Enforce tenant isolation, doctor own-scope rules, and server-side permissions. Exclude CSV export and platform-level dashboard.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F17 - Super Admin Support, Purge, Platform Audit

Goal: Let platform operators support clinics without unsafe impersonation.

Scope:

- Platform dashboard: clinic count, active doctors, appointment volume,
  no-show rates, WhatsApp delivery failure rate, AI fallback rate.
- Clinic list with active/soft-deleted filters.
- Clinic detail support view.
- Explicit super admin routes:
  `/api/v1/super-admin/clinics/{clinicId}/...`.
- Cross-clinic user, doctor, appointment, patient, FAQ support actions.
- SUPER_ADMIN user management.
- Two-person approval for adding/removing SUPER_ADMIN access.
- Block removal of last active SUPER_ADMIN.
- SUPER_ADMIN permissions are inferred from role, not stored in permission rows.
- Soft delete and restore support.
- GDPR permanent purge endpoint requiring explicit reason.
- Purge revokes related tokens where applicable.
- Audit log query/filter for super admin actions and purge events.

Out of scope:

- Clinic-user impersonation.
- Non-MVP MFA enforcement.
- In-app purge UI if API-only is acceptable.

Acceptance checks:

- SUPER_ADMIN cannot mutate normal clinic-scoped endpoints without explicit
  target clinic path.
- Every SUPER_ADMIN action is audited.
- Purge is irreversible, requires reason, and leaves audit fact.
- Last SUPER_ADMIN removal is blocked.
- Support views do not break tenant isolation for clinic users.

Speckit prompt:

```text
$speckit-specify Implement F17 Super Admin Support, Purge, Platform Audit for Cliniqo AI. SUPER_ADMIN users must access platform dashboard metrics, clinic list, clinic detail support views, explicit `/api/v1/super-admin/clinics/{clinicId}/...` routes for support actions, cross-clinic management of users/doctors/appointments/patients/FAQs, soft delete/restore, GDPR permanent purge requiring a reason, token revocation for deactivated or purged users, audit log query/filter, and platform aggregate metrics. SUPER_ADMIN user management must enforce two-person approval, block removal of the last active SUPER_ADMIN, infer permissions from role, and audit every action. Exclude clinic-user impersonation and non-MVP MFA enforcement.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## F18 - Observability, Health, Backup, Runbooks

Goal: Make the MVP production-operable.

Scope:

- Structured logs with request ID, trace ID, clinic ID when safe, user ID when
  safe, action, sanitized message.
- PHI and secret redaction.
- Trace propagation to OpenAI and WhatsApp clients.
- Metrics: HTTP requests, request duration, AI requests, AI cost, WhatsApp
  inbound/outbound, scheduler runs, scheduler lag, DB connections, booking
  intents, OTP attempts, notification failures.
- Health checks: liveness, readiness, info.
- Readiness includes DB, Flyway, WABA reachability cached check, OpenAI cached
  check.
- Alerts: webhook signature mismatch, scheduler lag, AI error rate, WhatsApp
  failure rate, DB pool saturation, 5xx rate, OpenAI cost spike.
- Backup and restore drill documentation.
- Runbooks for onboarding failure, WhatsApp token rotation, failed reminders,
  AI fallback, purge, and restore drill.

Out of scope:

- Kubernetes.
- Kafka/Redis/queue migration.
- Multi-region infrastructure.
- Distributed tracing infrastructure as a hard dependency.

Acceptance checks:

- Logs contain no PHI, secrets, medicine names, or patient messages.
- Readiness degrades safely during upstream outages.
- Metrics are named and labeled consistently.
- Alert thresholds map to owner/severity.
- Restore drill is documented and executable.

Speckit prompt:

```text
$speckit-specify Implement F18 Observability, Health, Backup, Runbooks for Cliniqo AI. Add PHI-safe structured logging with request IDs, trace IDs, clinic/user context where safe, action names, and redaction; propagate traces to OpenAI and WhatsApp clients; expose metrics for HTTP, AI, costs, WhatsApp, schedulers, DB, booking intents, OTP, and notification failures; provide liveness/readiness/info health checks with DB/Flyway/WABA/OpenAI cached readiness; define alerts for webhook signature mismatches, scheduler lag, AI error rate, WhatsApp failures, DB saturation, 5xx rate, and cost spikes; and document executable runbooks for onboarding failure, token rotation, failed reminders, AI fallback, purge, backups, and restore drills. Exclude Kubernetes, queue migration, multi-region infrastructure, and mandatory external tracing infrastructure.
$speckit-plan
$speckit-tasks
$speckit-implement
```

## Release Cuts

### Cut 1 - Internal Scheduling Core

Complete F01 through F08.

Result: A clinic can be onboarded, users can log in, doctors and schedules can
be configured, patients can be stored, receptionists can book appointments, and
reminder records are created.

### Cut 2 - WhatsApp Primary Channel

Complete F09 through F13.

Result: Patients can use WhatsApp for FAQ, booking, lookup, reschedule,
cancellation, handoff, reminders, and post-appointment status.

### Cut 3 - Public Growth and Retention

Complete F14 through F16.

Result: Clinics can publish a booking site, patients can opt into medicine
reminders, and staff can operate from dashboards and analytics.

### Cut 4 - Platform Operations

Complete F17 and F18.

Result: The platform team can support clinics, audit actions, handle purge
requests, and operate the MVP safely.

## Parallel Work Guidance

After F01 is complete, F02 and F04 can be specified in parallel, but integrate
carefully because onboarding creates the settings used by schedules.

After F06 and F07 are stable, these streams can proceed in parallel:

- Stream A: F08 receptionist dashboard.
- Stream B: F09-F13 WhatsApp and AI.
- Stream C: F14 public booking site.
- Stream D: F16 analytics.

Do not start F11, F12, F13, F14, or F15 before F06 and F07 stabilize. Otherwise
appointment and notification lifecycle rules will be reimplemented inconsistently.

## Future Scope Parking Lot

Do not include these in MVP Speckit specs unless product scope changes:

- Patient mobile app.
- Authenticated patient portal.
- Cross-clinic patient marketplace.
- Online payments.
- Clinical prescription management.
- Drug interaction checks.
- Pharmacy integrations.
- Multi-branch or multi-location clinic model.
- SUPER_ADMIN impersonation of clinic users.
- Kafka, CQRS, event sourcing, Kubernetes, or microservices.
- SMS/email as primary alternatives to WhatsApp.
- Voice AI.
- CSV export while marked future.
- Advanced AI clinical summaries or SOAP notes.
