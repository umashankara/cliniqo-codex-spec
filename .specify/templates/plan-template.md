# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]

**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

[Extract from feature spec: primary requirement + technical approach from research]

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: [e.g., Python 3.11, Swift 5.9, Rust 1.75 or NEEDS CLARIFICATION]

**Primary Dependencies**: [e.g., FastAPI, UIKit, LLVM or NEEDS CLARIFICATION]

**Storage**: [if applicable, e.g., PostgreSQL, CoreData, files or N/A]

**Testing**: [e.g., pytest, XCTest, cargo test or NEEDS CLARIFICATION]

**Target Platform**: [e.g., Linux server, iOS 15+, WASM or NEEDS CLARIFICATION]

**Project Type**: [e.g., library/cli/web-service/mobile-app/compiler/desktop-app or NEEDS CLARIFICATION]

**Performance Goals**: [domain-specific, e.g., 1000 req/s, 10k lines/sec, 60 fps or NEEDS CLARIFICATION]

**Constraints**: [domain-specific, e.g., <200ms p95, <100MB memory, offline-capable or NEEDS CLARIFICATION]

**Scale/Scope**: [domain-specific, e.g., 10k users, 1M LOC, 50 screens or NEEDS CLARIFICATION]

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Document pass/fail for each Cliniqo AI constitution gate. Any fail requires a scoped design
change or an explicit entry in Complexity Tracking.

- **Tenant isolation**: Explain how `clinic_id` is derived from trusted context only, how queries
  remain clinic-scoped, how cross-tenant access returns 404 without leaking IDs/counts, and how
  WhatsApp/public routes resolve tenant identity before any business service runs.
- **Appointment/channel lifecycle**: If the feature touches appointments or patient communication,
  explain channel parity, immutable `createdBy`, transaction boundaries, notification records, and
  retry/failure surfacing.
- **Modular monolith boundaries**: Identify affected Spring Boot modules and confirm controllers,
  services, repositories, DTOs, and cross-module service-interface calls follow architecture rules.
- **PHI/security/audit**: Identify PHI/secrets handled, redaction/encryption needs, consent rules,
  audit events, SUPER_ADMIN hardening impact, and purge/soft-delete impact.
- **Tests and observability**: List required contract/integration tests, mandatory cross-tenant
  leak tests, scheduler/retry/concurrency tests, OTP/CAPTCHA/rate-limit tests, webhook signature
  tests, logs/metrics, and operational dashboard surfacing.

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
backend/
├── src/
│   └── main/java/com/cliniqo/
│       ├── auth/
│       ├── clinic/
│       ├── doctor/
│       ├── patient/
│       ├── appointment/
│       ├── whatsapp/
│       ├── ai/
│       ├── notification/
│       ├── analytics/
│       ├── publicwebsite/
│       ├── medicinereminder/
│       ├── superadmin/
│       └── audit/
└── tests/

frontend/
├── src/
│   ├── components/
│   ├── pages/
│   ├── services/
│   ├── hooks/
│   ├── context/
│   ├── types/
│   └── routes/
└── tests/
```

**Structure Decision**: [Document the selected structure and reference the real
directories captured above]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
