<!--
Sync Impact Report
Version change: 1.1.0 -> 1.1.1
Modified principles:
- IV. PHI Security, Consent, And Auditability (removed external-document dependency)
Added sections:
- Self-contained MVP Product Scope
- Self-contained Required Stack And Architecture
- Self-contained Core Business Rules
Removed sections:
- External reference-file dependency wording
Templates requiring updates:
- checked .specify/templates/plan-template.md
- checked .specify/templates/spec-template.md
- checked .specify/templates/tasks-template.md
- checked .specify/templates/commands/*.md (directory absent)
- checked AGENTS.md
Follow-up TODOs:
- None
-->
# Cliniqo AI Constitution

## Core Principles

### I. Tenant Isolation Is Non-Negotiable
Every business capability MUST preserve clinic-level isolation by default. Each clinic-scoped
business record MUST carry `clinic_id`, and tenant identity MUST be derived only from trusted
server context: JWT-backed `TenantContext`, website-slug-backed `WebsiteContext`, or explicit
SUPER_ADMIN route context. Tenant-scoped request DTOs MUST NOT accept `clinicId`; if clients send
one, the server MUST ignore it, log a tamper event, and continue using trusted context.

Clinic users MUST receive no cross-tenant identifiers, counts, rows, or existence signals. Attempts
to access another clinic's record MUST return `404 Not Found`, not `403 Forbidden`. SUPER_ADMIN is
the only cross-tenant principal and MUST act through explicit `/api/v1/super-admin/clinics/{clinicId}`
routes with complete audit records.

WhatsApp webhook tenant resolution MUST use the receiving `phone_number_id` or equivalent trusted
WhatsApp metadata, then verify the payload signature using that clinic's stored app secret before
any patient message, conversation, AI, or appointment service processes the payload. Invalid
signatures MUST be rejected and audit logged.

Rationale: Cliniqo is a healthcare SaaS platform. A single cross-clinic leak is a critical product,
security, and trust failure.

### II. Appointment Integrity And Channel Parity
All appointment creation channels, including WhatsApp AI, receptionist/manual entry, per-clinic
website, clinic admin, and SUPER_ADMIN support actions, MUST write to the same appointment model
and downstream lifecycle. `createdBy` MUST be set at creation, remain immutable, and drive analytics.
Every confirmed appointment MUST enter the same confirmation, reminder, post-appointment follow-up,
and optional medicine-reminder flow.

Appointment booking, rescheduling, cancellation, status changes, patient upsert, notification
record creation, and audit writes MUST be transactionally consistent. The system MUST prevent
double booking with database constraints and/or row locking, not only application checks. External
side effects such as WhatsApp sends MUST be triggered from persisted notification records after
commit, with retry and failure surfacing.

Rationale: Patients and clinics must receive the same reliable service regardless of how a booking
was initiated, and no user may see a booked state that was not durably persisted.

### III. Modular Monolith Boundaries
The MVP MUST remain a Spring Boot modular monolith with clean architecture boundaries and
domain-driven modules. Microservices, event sourcing, CQRS, Kafka, Kubernetes, and distributed
transaction designs are forbidden for MVP unless this constitution is amended.

Modules MUST communicate through service interfaces, never through another module's repositories,
controllers, or entities. Controllers MUST stay thin; business rules live in services; repositories
perform persistence only; DTOs define API contracts and MUST NOT expose JPA entities directly. The
AI module MUST remain stateless, with no database access and no authority to execute business logic
or authorization decisions.

Rationale: A modular monolith gives the MVP strong consistency, faster delivery, and a viable
future extraction path without premature distributed-system cost.

### IV. PHI Security, Consent, And Auditability
Patient data, contact details, medical context, medicine names, WhatsApp credentials, tokens, and
other secrets MUST be protected by design. PHI MUST NOT appear in application logs, notification
records, AI prompt logs, audit diffs, or error messages. Sensitive stored values, including
WhatsApp credentials and patient-entered medicine details, MUST be encrypted at rest.

Patient-facing medicine reminders MUST be explicit opt-in only and MUST remain a convenience
feature, not a clinical prescription system. Permanent purge is SUPER_ADMIN-only, requires an
audited reason, and MUST preserve the fact of deletion while removing the protected content. Every
permission change, SUPER_ADMIN action, status override, purge, tenant tamper event, and failed
security validation MUST be audit logged with PHI-redacted metadata.

SUPER_ADMIN access MUST remain rare and hardened. The system MUST keep at least one active
SUPER_ADMIN at all times, MUST audit every SUPER_ADMIN-on-SUPER_ADMIN change, and MUST require the
two-person approval flow defined by the architecture before adding another SUPER_ADMIN or removing
SUPER_ADMIN access. SUPER_ADMIN permissions MUST be inferred from role, not editable permission
rows.

Rationale: Cliniqo handles healthcare-adjacent data and operational trust depends on traceable,
minimal, consent-aware processing.

### V. Testable, Observable Healthcare Workflows
Every feature that touches tenant-scoped data, appointments, WhatsApp routing, public website
booking, permissions, audit, AI intent execution, soft delete/restore/purge, or notification
scheduling MUST include tests that prove the critical contract. Cross-tenant leak tests are
mandatory for every clinic-scoped endpoint added or changed and a failure is P0.

Features MUST define measurable service behavior before implementation: response-time budgets,
scheduler tolerance, retry policy, idempotency, fallback behavior, and dashboard failure surfacing
where applicable. Logs MUST include request IDs and clinic context without PHI. Operational failures
MUST be visible to the owning clinic or SUPER_ADMIN as appropriate and MUST NOT be silent.

Public booking and webhook features MUST define abuse controls and executable tests before
implementation, including CAPTCHA or honeypot behavior, OTP TTL/resend/attempt limits, rate limits,
idempotency, webhook signature verification, tenant resolution, and failure/audit behavior.

Rationale: Scheduling, communication, and tenant boundaries are safety-critical workflows for this
product; they need executable proof and operational visibility.

## Product And Architecture Constraints

This constitution is self-contained and remains authoritative even when earlier discovery,
requirements, or architecture notes are absent. Feature specifications and implementation plans
MUST derive product behavior, technical structure, scope boundaries, and review gates from this
constitution unless they explicitly propose a constitution amendment.

### MVP Product Scope

Cliniqo AI is a multi-tenant clinic appointment management and automation SaaS. Its MVP MUST
support these actors:

- SUPER_ADMIN: platform operator who onboards clinics, supports clinics, manages platform users,
  views cross-clinic operational data, and performs audited purge actions.
- CLINIC_ADMIN: clinic operator who manages clinic settings, doctors, staff users, schedules,
  FAQs, WhatsApp configuration, dashboards, and permissions within one clinic.
- RECEPTIONIST: clinic staff member who manages appointments, patients, daily schedules, failed
  reminders, and escalated WhatsApp conversations within one clinic.
- DOCTOR: clinic staff member who views their own appointment schedule and permitted patient visit
  details within one clinic.
- Patient: unauthenticated end user who interacts only through WhatsApp or a per-clinic public
  booking website. Patients are not system login users in MVP.

The MVP MUST support these appointment creation channels, all writing the same appointment model:

- WhatsApp AI: `createdBy = whatsapp-ai`; patient identified by sender phone and resolved clinic.
- Receptionist/manual entry: `createdBy = manual-receptionist`; patient identified by entered
  phone and name.
- Per-clinic public website: `createdBy = clinic-website`; patient identified by verified phone
  and name.
- Clinic admin entry: `createdBy = clinic-admin`; used for special clinic-handled bookings.
- SUPER_ADMIN support action: `createdBy = super-admin`; used only through explicit support routes.

All appointment channels MUST share the same patient upsert rules, availability validation,
double-booking prevention, transactional persistence, confirmation flow, reminders, post-visit
follow-up, and optional medicine-reminder entry point.

The MVP MUST include these business capabilities:

- Clinic onboarding with first clinic admin, slug, timezone, working hours, slot duration, default
  language, reminder windows, cutoff settings, and WhatsApp business metadata.
- Authentication, role enforcement, granular per-module permissions, server-driven navigation, and
  first-login password reset for temporary passwords.
- Doctor profiles, schedules, working hours, availability, and clinic-local timezone behavior.
- Patient records scoped by `(clinic_id, phone)`, with the same phone allowed in separate clinics
  as separate patient records.
- Appointment booking, rescheduling, cancellation, lookup, status lifecycle, source attribution,
  audit, idempotency, and concurrency protection.
- WhatsApp webhook handling, per-clinic outbound messaging, AI intent classification, FAQ
  automation, appointment booking, reschedule, cancellation, lookup, and receptionist handoff.
- Notification records, appointment confirmations, reminders, retry handling, delivery status, and
  failed-reminder surfacing.
- Receptionist operational dashboard for today's appointments, patient search, manual booking,
  appointment actions, conversation handoff, and failed reminders.
- Public per-clinic booking website with slug resolution, doctor/slot browsing, OTP verification,
  CAPTCHA or honeypot, rate limits, idempotent confirmation, and privacy-safe pages.
- Post-appointment follow-up with auto-marked COMPLETED or NO_SHOW status, verification badge,
  fallback behavior, and staff override audit.
- Patient-voluntary medicine reminders with explicit opt-in, patient-defined courses, pause/resume,
  stop/update/list controls, DONE/SKIP adherence tracking, PHI-safe handling, and a non-clinical
  disclaimer.
- Clinic analytics for appointments, no-shows, cancellations, patient retention, source mix,
  doctors, peak hours, reminder effectiveness, and needs-verification workflows.
- SUPER_ADMIN platform dashboard, clinic support views, audit log access, user support actions,
  soft delete/restore, and GDPR-style purge with reason.

### Required Stack And Architecture

Required stack and structure for MVP:
- Backend: Spring Boot modular monolith, Java, clean architecture, domain modules, REST APIs.
- Frontend: React SPA with TypeScript, TailwindCSS, Vite, role-aware rendering backed by server
  authorization.
- Storage: PostgreSQL, UUID primary keys, Flyway migrations, soft delete for business records,
  schema changes via migrations only.
- Tenancy: shared database with `clinic_id` discriminator on all business tables except documented
  platform/infrastructure tables.
- External services: OpenAI API as stateless NLP provider; WhatsApp Business API with one WABA and
  one dedicated phone number per clinic.
- Deployment target: Vercel frontend, Railway/Docker backend, Railway PostgreSQL for MVP.

The backend MUST be organized into domain modules with clear boundaries: auth, clinic, doctor,
patient, appointment, whatsapp, ai, notification, analytics, public-website, medicine-reminder,
super-admin, and audit. Each module MUST follow controller, service, repository, entity, DTO,
mapper, and enum layering where applicable. Controllers validate and delegate; services hold
business logic and transactions; repositories perform persistence only; DTOs define API contracts.

The tenant model MUST use one shared PostgreSQL database with `clinic_id` discriminator columns on
all business tables except platform/infrastructure tables. Tenant isolation MUST be enforced in
depth at the edge/context layer, controller layer, service layer, repository layer, and database
constraint/index layer. SUPER_ADMIN is the only cross-tenant principal and uses explicit platform
routes rather than tenant-scoped endpoints.

The AI module MUST be stateless. It may classify intent, extract structured data, and generate
language-specific replies, but it MUST NOT access the database, execute business logic, make
authorization decisions, or mutate state directly. All AI-suggested actions MUST pass through
deterministic domain services.

WhatsApp integration MUST use one dedicated WhatsApp Business Account and phone number per clinic.
Inbound webhooks MUST resolve tenant identity from trusted WhatsApp metadata before processing.
Outbound sends MUST use the resolved clinic's credentials only. Message templates are per-clinic
business assets and notification dispatch MUST use persisted notification records after commit.

Public website routes MUST resolve clinic identity from the website slug into trusted
`WebsiteContext`. Public endpoints MUST NOT return patient names, patient history, message bodies,
or any cross-clinic discovery data. Public booking write operations MUST pass through appointment
services rather than direct persistence.

### Core Business Rules

Appointment rules:
- A doctor can have only one active appointment per slot. This MUST be enforced by database
  constraints and/or row locking in addition to service validation.
- Appointment slot duration MUST be one of 15, 20, 30, 45, or 60 minutes per clinic.
- Appointments MUST be booked only during doctor working hours and never in the past.
- Maximum advance booking MUST be configurable per clinic, default 30 days, allowed range 7 to 90
  days.
- Minimum booking notice MUST be configurable per clinic, default 60 minutes, allowed range 15
  minutes to 24 hours.
- Cancellation cutoff MUST be configurable per clinic, default 120 minutes, allowed range 0 to 1440
  minutes. Patient cancellation outside the allowed window MUST fail with a typed validation error
  unless a permitted clinic role overrides it and the override is audited.
- Rescheduling MUST use W1-W5 windows: W1 more than 24 hours before, W2 from 24 hours to 2 hours
  before, W3 from 2 hours before to scheduled time, W4 after scheduled time within 24 hours, and W5
  more than 24 hours after scheduled time. W5 patient reschedule is blocked without receptionist or
  clinic-admin override.
- Rescheduling MUST create or link to the appropriate new scheduled appointment while preserving
  original source attribution for analytics and writing an audit trail for the actor who performed
  the change.

Notification and reminder rules:
- Appointment reminders MUST be sent only for SCHEDULED appointments.
- Default appointment reminders MUST include morning-of reminder at 08:00 clinic-local time and a
  2-hour-before reminder, both configurable per clinic.
- Failed delivery retry MUST use 5 attempts total with fixed backoff: 1 minute, 5 minutes, 15
  minutes, 60 minutes, and 4 hours. Final failure MUST be visible to clinic staff.
- Patients MAY opt out of appointment reminders per clinic.
- External sends MUST NOT be part of the core database transaction; persisted notification intent
  MUST be committed first, then dispatched after commit.

Post-appointment rules:
- Follow-up MUST ask whether the patient attended after the configured delay.
- Patient "attended" response may auto-mark COMPLETED with a verification badge.
- Patient "did not attend" response may auto-mark NO_SHOW with a verification badge.
- Lack of response after the fallback window may auto-mark NO_SHOW with a verification badge.
- Staff override MUST clear or update the verification state and write an audit event.

Public booking rules:
- Clinic slug MUST be unique, URL-safe, lower-case, and platform-controlled.
- OTP verification MUST happen before final booking confirmation.
- OTP TTL MUST be 5 minutes, resend cooldown 60 seconds, maximum 3 resends per booking intent,
  maximum 5 verification attempts per booking intent, then lockout.
- Rate limits MUST exist per phone, IP, and clinic slug.
- CAPTCHA or honeypot MUST protect first booking/OTP initiation.
- Disabled public website MUST show a friendly unavailable state without leaking tenant details.

Medicine reminder rules:
- Medicine reminders MUST be opt-in only and patient-defined.
- Reminder lead time MUST default to 10 minutes and be configurable per schedule from 0 to 60
  minutes.
- Patients MUST be able to list, pause, resume, update, and stop medicine reminders through
  WhatsApp.
- Medicine names and patient-entered medicine details MUST be treated as sensitive data.
- This feature MUST remain a convenience reminder tool, not clinical prescription management.

SUPER_ADMIN rules:
- SUPER_ADMIN tokens have no clinic scope and MUST use explicit platform/support routes.
- SUPER_ADMIN permissions are inferred from role and MUST NOT be stored as editable permission rows.
- At least one active SUPER_ADMIN MUST always exist.
- Adding another SUPER_ADMIN or removing SUPER_ADMIN access MUST use two-person approval when the
  affected action is supported by the platform flow.
- SUPER_ADMIN writes on behalf of a clinic MUST carry the target clinic in the route/context and
  audit record.
- Permanent purge is SUPER_ADMIN-only, requires a non-empty reason, revokes affected security
  tokens, removes protected content, and preserves the fact of deletion in audit.

Operational rules:
- All mutating user-facing operations MUST execute inside a single database transaction for their
  durable state changes and audit writes.
- Idempotency MUST protect mutating operations that may be retried by clients, webhooks, or public
  booking flows.
- Logs MUST include request IDs and safe clinic/user context but MUST NOT include PHI, secrets,
  medicine names, or patient message bodies.
- Health checks, metrics, alerts, and runbooks MUST make upstream failures, scheduler failures,
  webhook signature mismatches, notification failures, and security events diagnosable.

The following MVP boundaries are mandatory unless amended: no patient mobile app, no authenticated
patient portal, no cross-clinic patient marketplace, no clinical prescription management, no online
payments, no multi-branch clinic model, and no SUPER_ADMIN impersonation.

## Delivery Gates And Review Process

Before implementation, each feature plan MUST pass a Constitution Check covering tenant isolation,
appointment/channel lifecycle impact, module boundaries, PHI/security/audit impact, and required
tests/observability. Any violation MUST be documented with a simpler rejected alternative and cannot
proceed without explicit approval through a constitution amendment or feature-scope correction.

Feature specifications MUST include edge cases for tenant isolation, authorization, patient identity
by `(clinic_id, phone)`, concurrency, retry/failure handling, timezone behavior, and auditability
when the feature touches those areas. Task lists MUST include concrete test, migration, audit,
logging, and documentation tasks whenever the feature affects those contracts.

Reviews MUST block changes that:
- accept tenant identity from untrusted client input on tenant-scoped endpoints;
- omit `clinic_id` predicates or defense-in-depth filters on business-table queries;
- perform appointment state changes outside a single transactional boundary;
- bypass module service interfaces;
- log PHI, secrets, medicine names, or patient message bodies;
- add AI behavior that directly mutates state without deterministic service validation;
- add public booking behavior without OTP TTL, resend/attempt limits, CAPTCHA or honeypot, rate
  limits, idempotency, and tenant resolution;
- add webhook behavior without per-clinic tenant resolution, signature verification, rate limiting,
  idempotency, and audit records for rejected or unmapped payloads;
- add or remove SUPER_ADMIN access without preserving at least one active SUPER_ADMIN, two-person
  approval where required, and complete audit records.

## Governance

This constitution supersedes conflicting local practices and is binding for all Cliniqo AI feature
specifications, plans, tasks, reviews, and implementation work. Any previous notes, drafts, or
generated feature backlogs are advisory only. When any artifact conflicts with this constitution,
this constitution wins until amended.

Amendments require:
- a written rationale describing the changed principle, constraint, or governance rule;
- an impact review across this constitution, templates, active specs, plans, and task lists;
- a semantic version bump: MAJOR for removing or redefining principles, MINOR for adding principles
  or materially expanding governance, PATCH for clarifications that do not change obligations;
- updated Sync Impact Report at the top of this file.

Every generated plan MUST re-check constitution compliance before Phase 0 research and after Phase
1 design. Every generated task list MUST preserve traceability from user stories to tests and
implementation tasks. Manual code review remains responsible for enforcing constitution gates when
automation cannot.

**Version**: 1.1.1 | **Ratified**: 2026-06-13 | **Last Amended**: 2026-06-13
