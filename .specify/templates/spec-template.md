# Feature Specification: [FEATURE NAME]

**Feature Branch**: `[###-feature-name]`

**Created**: [DATE]

**Status**: Draft

**Input**: User description: "$ARGUMENTS"

## User Scenarios & Testing *(mandatory)*

<!--
  IMPORTANT: User stories should be PRIORITIZED as user journeys ordered by importance.
  Each user story/journey must be INDEPENDENTLY TESTABLE - meaning if you implement just ONE of them,
  you should still have a viable MVP (Minimum Viable Product) that delivers value.

  Assign priorities (P1, P2, P3, etc.) to each story, where P1 is the most critical.
  Think of each story as a standalone slice of functionality that can be:
  - Developed independently
  - Tested independently
  - Deployed independently
  - Demonstrated to users independently
-->

### User Story 1 - [Brief Title] (Priority: P1)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently - e.g., "Can be fully tested by [specific action] and delivers [specific value]"]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]
2. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

### User Story 2 - [Brief Title] (Priority: P2)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

### User Story 3 - [Brief Title] (Priority: P3)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

[Add more user stories as needed, each with an assigned priority]

### Edge Cases

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right edge cases.
-->

- Tenant isolation: What happens when a clinic user targets another clinic's record?
- Client tampering: What happens if request input includes `clinicId` or another tenant hint?
- Patient identity: How is the same phone number handled across different clinics?
- Concurrency: What happens when two actors attempt the same booking, status change, or update?
- Failure and retry: How are WhatsApp/OpenAI/scheduler failures surfaced and recovered?
- Public/webhook security: What OTP, CAPTCHA/honeypot, rate-limit, signature verification, and
  idempotency behavior applies?
- SUPER_ADMIN safety: Does this affect SUPER_ADMIN access, two-person approval, last-admin
  protection, support actions, or cross-clinic auditability?
- Timezone: Which clinic-local time rules apply to reminders, bookings, and date boundaries?
- Auditability: Which state changes, permission changes, overrides, or purge actions are logged?

## Requirements *(mandatory)*

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right functional requirements.
-->

### Functional Requirements

- **FR-001**: System MUST [specific capability, e.g., "allow receptionists to create appointments"]
- **FR-002**: System MUST derive tenant identity from trusted server context, not request body input
- **FR-003**: System MUST enforce role and granular permission checks server-side
- **FR-004**: System MUST persist required state changes transactionally with audit records
- **FR-005**: System MUST avoid logging PHI, secrets, medicine names, and patient message bodies
- **FR-006**: System MUST expose operational failure states to the owning clinic or SUPER_ADMIN
- **FR-007**: System MUST enforce public booking OTP/CAPTCHA/rate-limit controls and webhook
  signature verification when those surfaces are affected

*Example of marking unclear requirements:*

- **FR-007**: System MUST authenticate users via [NEEDS CLARIFICATION: auth method not specified - email/password, SSO, OAuth?]
- **FR-008**: System MUST retain user data for [NEEDS CLARIFICATION: retention period not specified]

### Key Entities *(include if feature involves data)*

- **[Entity 1]**: [What it represents, key attributes without implementation]
- **[Entity 2]**: [What it represents, relationships to other entities]

### Constitution Alignment *(mandatory)*

- **Tenant Isolation**: [How clinic scope is derived and enforced; include 404 behavior for
  cross-tenant access when applicable]
- **Appointment/Channel Lifecycle**: [Impact on `createdBy`, reminders, follow-up, status changes,
  notifications, and transaction boundaries, or N/A]
- **PHI/Security/Audit**: [PHI handled, redaction/encryption needs, consent, audit events, and
  purge/soft-delete implications, including SUPER_ADMIN safeguards when applicable]
- **Module Boundaries**: [Affected modules and service-interface interactions]
- **Required Tests**: [Cross-tenant, contract, integration, concurrency, scheduler, retry, or
  authorization tests required by this feature, including OTP/CAPTCHA/rate-limit or webhook
  signature tests when applicable]

## Success Criteria *(mandatory)*

<!--
  ACTION REQUIRED: Define measurable success criteria.
  These must be technology-agnostic and measurable.
-->

### Measurable Outcomes

- **SC-001**: [Measurable metric, e.g., "Users can complete account creation in under 2 minutes"]
- **SC-002**: [Measurable metric, e.g., "System handles 1000 concurrent users without degradation"]
- **SC-003**: [User satisfaction metric, e.g., "90% of users successfully complete primary task on first attempt"]
- **SC-004**: [Business metric, e.g., "Reduce support tickets related to [X] by 50%"]

## Assumptions

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right assumptions based on reasonable defaults
  chosen when the feature description did not specify certain details.
-->

- [Assumption about target users, e.g., "Users have stable internet connectivity"]
- [Assumption about scope boundaries, e.g., "Mobile support is out of scope for v1"]
- [Assumption about data/environment, e.g., "Existing authentication system will be reused"]
- [Dependency on existing system/service, e.g., "Requires access to the existing user profile API"]
