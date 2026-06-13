# Tasks: F01 Foundation, Tenancy, Auth, Audit

**Input**: Design documents from `/specs/001-foundation-auth-audit/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/foundation-api.openapi.yaml, quickstart.md

**Tests**: Required by the feature specification for authentication/session behavior, tenant isolation, audit transactionality, response envelopes, PHI-safe logging, soft delete, forbidden `clinicId` contracts, and Clinic A versus Clinic B integration coverage.

**Organization**: Tasks are grouped by user story so each story can be implemented and tested as an independent increment after the shared foundation is complete.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel because it touches different files and has no dependency on incomplete tasks in the same phase
- **[Story]**: User story label for story phases only
- All tasks include exact file paths

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create the backend Maven project shell and baseline development structure.

- [X] T001 Create backend Maven directory structure in backend/src/main/java/com/cliniqo/ and backend/src/test/java/com/cliniqo/
- [X] T002 Create Spring Boot Maven project descriptor with Java 21 and pinned dependencies in backend/pom.xml
- [X] T003 [P] Create application entrypoint in backend/src/main/java/com/cliniqo/CliniqoApplication.java
- [X] T004 [P] Create local/test/prod configuration skeleton in backend/src/main/resources/application.yml
- [X] T005 [P] Create initial package marker files in backend/src/main/java/com/cliniqo/common/, backend/src/main/java/com/cliniqo/auth/, backend/src/main/java/com/cliniqo/audit/, backend/src/main/java/com/cliniqo/clinic/, backend/src/main/java/com/cliniqo/publicwebsite/, and backend/src/main/java/com/cliniqo/superadmin/
- [X] T006 [P] Configure OpenAPI metadata for the foundation API in backend/src/main/java/com/cliniqo/config/OpenApiConfig.java
- [X] T007 [P] Create test container support base class in backend/src/test/java/com/cliniqo/support/PostgresIntegrationTest.java
- [X] T008 [P] Create test profile configuration in backend/src/test/resources/application-test.yml
- [X] T009 [P] Copy foundation OpenAPI contract into backend/src/test/resources/contracts/foundation-api.openapi.yaml

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish database, base entities, response/error primitives, security scaffolding, logging, and fixtures required by all stories.

**Critical**: No user story work begins until this phase is complete.

- [X] T010 Create baseline Flyway migration for clinics, users, refresh_sessions, audit_events, and tenant_probe_records in backend/src/main/resources/db/migration/V001__foundation_schema.sql
- [X] T011 Create seed/test fixture migration for Clinic A, Clinic B, SUPER_ADMIN, clinic users, and tenant probe records in backend/src/main/resources/db/migration/V002__foundation_test_fixtures.sql
- [X] T012 [P] Implement AuditableRecord base class in backend/src/main/java/com/cliniqo/common/entity/AuditableRecord.java
- [X] T013 [P] Implement BusinessRecord base class with UUID, audit columns, tenant field, and soft-delete metadata in backend/src/main/java/com/cliniqo/common/entity/BusinessRecord.java
- [X] T014 [P] Define role, user status, clinic status, audit event type, and typed error enums in backend/src/main/java/com/cliniqo/common/enums/
- [X] T015 [P] Implement global response envelope, error body, and metadata DTOs in backend/src/main/java/com/cliniqo/common/dto/
- [X] T016 [P] Implement typed exception hierarchy in backend/src/main/java/com/cliniqo/common/exception/
- [X] T017 Implement global exception handler with safe typed responses in backend/src/main/java/com/cliniqo/common/exception/GlobalExceptionHandler.java
- [X] T018 [P] Implement request ID filter and MDC population in backend/src/main/java/com/cliniqo/config/RequestIdFilter.java
- [X] T019 [P] Implement PHI redaction converter in backend/src/main/java/com/cliniqo/common/logging/PhiRedactionConverter.java
- [X] T020 Configure PHI-safe local/prod Logback appenders in backend/src/main/resources/logback-spring.xml
- [X] T021 [P] Implement user principal and JWT claim model in backend/src/main/java/com/cliniqo/common/security/UserPrincipal.java
- [X] T022 [P] Implement JWT token provider using JJWT in backend/src/main/java/com/cliniqo/common/security/JwtTokenProvider.java
- [X] T023 Implement JWT authentication filter in backend/src/main/java/com/cliniqo/common/security/JwtAuthenticationFilter.java
- [X] T024 Implement Spring Security configuration for auth routes, clinic routes, and SUPER_ADMIN placeholders in backend/src/main/java/com/cliniqo/config/SecurityConfig.java
- [X] T025 [P] Implement Clinic, User, RefreshSession, AuditEvent, and TenantProbeRecord entities in backend/src/main/java/com/cliniqo/
- [X] T026 [P] Implement repositories for Clinic, User, RefreshSession, AuditEvent, and TenantProbeRecord in backend/src/main/java/com/cliniqo/
- [X] T027 [P] Implement TenantContext holder in backend/src/main/java/com/cliniqo/clinic/context/TenantContext.java
- [X] T028 [P] Implement WebsiteContext placeholder in backend/src/main/java/com/cliniqo/publicwebsite/context/WebsiteContext.java
- [X] T029 [P] Implement SuperAdminRouteContext placeholder in backend/src/main/java/com/cliniqo/superadmin/context/SuperAdminRouteContext.java
- [X] T030 Implement tenant context filter and Hibernate tenant filter activation in backend/src/main/java/com/cliniqo/clinic/context/TenantContextFilter.java
- [X] T031 [P] Implement audit service interface and same-transaction writer skeleton in backend/src/main/java/com/cliniqo/audit/service/AuditService.java
- [X] T032 [P] Create authentication DTOs from contract in backend/src/main/java/com/cliniqo/auth/dto/
- [X] T033 [P] Create reusable integration fixture helpers for Clinic A, Clinic B, users, and tokens in backend/src/test/java/com/cliniqo/support/FoundationFixtures.java

**Checkpoint**: Foundation scaffolding is ready; user stories can now be implemented and tested independently.

---

## Phase 3: User Story 1 - Authenticate Clinic Users Safely (Priority: P1)

**Goal**: Clinic staff and platform operators can sign in, refresh sessions, and lose access after logout/revocation.

**Independent Test**: A user signs in, receives an authenticated session with actor and clinic scope, refreshes with rotation, and cannot use revoked/reused sessions.

### Tests for User Story 1

- [X] T034 [P] [US1] Create contract tests for /auth/login, /auth/refresh, and /auth/logout in backend/src/test/java/com/cliniqo/auth/AuthContractTest.java
- [X] T035 [P] [US1] Create integration tests for successful clinic-user and SUPER_ADMIN login in backend/src/test/java/com/cliniqo/auth/AuthLoginIT.java
- [X] T036 [P] [US1] Create integration tests for refresh rotation, reuse detection, family revocation, logout, expired sessions, and deactivated users in backend/src/test/java/com/cliniqo/auth/RefreshSessionIT.java
- [X] T037 [P] [US1] Create security tests proving tokens, passwords, and hashes never appear in responses or audit metadata in backend/src/test/java/com/cliniqo/auth/AuthPhiSafetyIT.java

### Implementation for User Story 1

- [X] T038 [P] [US1] Implement password hashing and credential validation service in backend/src/main/java/com/cliniqo/auth/service/CredentialService.java
- [X] T039 [P] [US1] Implement refresh session hashing, rotation, reuse detection, and family revocation in backend/src/main/java/com/cliniqo/auth/service/RefreshSessionService.java
- [X] T040 [US1] Implement authentication application service with login, refresh, and logout flows in backend/src/main/java/com/cliniqo/auth/service/AuthService.java
- [X] T041 [US1] Implement auth controller for /api/v1/auth/login, /api/v1/auth/refresh, and /api/v1/auth/logout in backend/src/main/java/com/cliniqo/auth/controller/AuthController.java
- [X] T042 [US1] Add authentication and refresh-session audit events in backend/src/main/java/com/cliniqo/auth/service/AuthAuditPublisher.java
- [X] T043 [US1] Wire authenticated user loading and active clinic validation in backend/src/main/java/com/cliniqo/common/security/CurrentUserService.java
- [X] T044 [US1] Run and fix US1 auth contract and integration tests in backend/src/test/java/com/cliniqo/auth/

**Checkpoint**: User Story 1 is fully functional and independently testable.

---

## Phase 4: User Story 2 - Enforce Clinic Tenant Isolation (Priority: P1)

**Goal**: Clinic users can access only their clinic's data, never provide tenant authority, and receive tenant-safe 404 responses for cross-clinic access.

**Independent Test**: Clinic A user can access Clinic A fixture data but cannot read, list, update, delete, or infer Clinic B data.

### Tests for User Story 2

- [X] T045 [P] [US2] Create contract test proving clinic-user request schemas do not accept clinicId in backend/src/test/java/com/cliniqo/tenancy/ClinicIdContractTest.java
- [X] T046 [P] [US2] Create integration test for missing tenant context rejection in backend/src/test/java/com/cliniqo/tenancy/MissingTenantContextIT.java
- [X] T047 [P] [US2] Create integration tests for Clinic A versus Clinic B read/list/update/delete/inference attempts in backend/src/test/java/com/cliniqo/tenancy/CrossTenantIsolationIT.java
- [X] T048 [P] [US2] Create integration test for request-body clinicId tampering and audit event creation in backend/src/test/java/com/cliniqo/tenancy/TenantTamperIT.java
- [X] T049 [P] [US2] Create architecture test for tenant-blind repository methods and forbidden clinicId DTO fields in backend/src/test/java/com/cliniqo/tenancy/TenancyArchitectureTest.java

### Implementation for User Story 2

- [X] T050 [P] [US2] Implement tenant tamper detector for request bodies and query payloads in backend/src/main/java/com/cliniqo/clinic/context/TenantTamperDetector.java
- [X] T051 [P] [US2] Implement tenant-aware repository helpers/specifications in backend/src/main/java/com/cliniqo/common/repository/TenantScopedRepositorySupport.java
- [X] T052 [US2] Implement tenant probe service with read, list, update, delete, and inference-safe behavior in backend/src/main/java/com/cliniqo/clinic/service/TenantProbeService.java
- [X] T053 [US2] Implement tenant probe controller for /api/v1/foundation/tenant-probe/{recordId} in backend/src/main/java/com/cliniqo/clinic/controller/TenantProbeController.java
- [X] T054 [US2] Ensure cross-tenant not-found mapping and no existence details in backend/src/main/java/com/cliniqo/common/exception/GlobalExceptionHandler.java
- [X] T055 [US2] Implement SUPER_ADMIN context probe route placeholder in backend/src/main/java/com/cliniqo/superadmin/controller/SuperAdminContextProbeController.java
- [X] T056 [US2] Add tenant tamper and SUPER_ADMIN target-context audit events in backend/src/main/java/com/cliniqo/audit/service/AuditService.java
- [X] T057 [US2] Run and fix US2 tenant isolation, tamper, and architecture tests in backend/src/test/java/com/cliniqo/tenancy/

**Checkpoint**: User Stories 1 and 2 both work independently.

---

## Phase 5: User Story 3 - Record Auditable Foundation Activity (Priority: P2)

**Goal**: Security-sensitive and mutating foundation actions produce durable PHI-redacted audit events that commit or roll back with their parent action.

**Independent Test**: A successful mutating action commits its audit event with the parent action; a rolled-back action leaves no successful orphan audit record.

### Tests for User Story 3

- [X] T058 [P] [US3] Create integration tests for same-transaction audit commit and rollback in backend/src/test/java/com/cliniqo/audit/AuditTransactionIT.java
- [X] T059 [P] [US3] Create integration tests for audit metadata redaction of tokens, credentials, phone numbers, and patient message text in backend/src/test/java/com/cliniqo/audit/AuditRedactionIT.java
- [X] T060 [P] [US3] Create integration tests for audit event categories covering auth, refresh, tenant tamper, soft delete, and SUPER_ADMIN target context in backend/src/test/java/com/cliniqo/audit/AuditEventTypeIT.java

### Implementation for User Story 3

- [X] T061 [P] [US3] Implement audit metadata redaction utilities in backend/src/main/java/com/cliniqo/audit/service/AuditMetadataRedactor.java
- [X] T062 [P] [US3] Implement audit event mapper and DTOs in backend/src/main/java/com/cliniqo/audit/dto/
- [X] T063 [US3] Complete same-transaction audit writer implementation in backend/src/main/java/com/cliniqo/audit/service/DefaultAuditService.java
- [X] T064 [US3] Integrate audit writer with auth, refresh, tenant tamper, soft-delete, and SUPER_ADMIN context foundation flows in backend/src/main/java/com/cliniqo/
- [X] T065 [US3] Add test-only rollback action for audit transaction validation in backend/src/test/java/com/cliniqo/audit/support/AuditRollbackFixture.java
- [X] T066 [US3] Run and fix US3 audit transaction and redaction tests in backend/src/test/java/com/cliniqo/audit/

**Checkpoint**: User Stories 1, 2, and 3 work independently with audit guarantees.

---

## Phase 6: User Story 4 - Return Consistent Safe Service Errors (Priority: P3)

**Goal**: All responses and logs have a predictable safe shape with request IDs, typed errors, and no PHI, secrets, or stack traces.

**Independent Test**: Successful responses, validation errors, authentication errors, authorization errors, tenant-safe 404s, conflicts, and unexpected failures all include request IDs and safe typed payloads.

### Tests for User Story 4

- [X] T067 [P] [US4] Create response envelope contract tests for success and typed error payloads in backend/src/test/java/com/cliniqo/common/ApiEnvelopeContractTest.java
- [X] T068 [P] [US4] Create request ID propagation tests for headers, responses, logs, and audit events in backend/src/test/java/com/cliniqo/common/RequestIdPropagationIT.java
- [X] T069 [P] [US4] Create PHI-safe logging tests for credentials, tokens, phone numbers, patient messages, and stack traces in backend/src/test/java/com/cliniqo/common/PhiSafeLoggingIT.java
- [X] T070 [P] [US4] Create typed exception mapping tests for validation, auth, forbidden, not-found, tenancy, conflict, and unexpected errors in backend/src/test/java/com/cliniqo/common/GlobalExceptionHandlerIT.java

### Implementation for User Story 4

- [X] T071 [P] [US4] Finalize ApiResponse, ApiError, ApiMeta, and validation error DTOs in backend/src/main/java/com/cliniqo/common/dto/
- [X] T072 [P] [US4] Finalize typed exception classes and error code mapping in backend/src/main/java/com/cliniqo/common/exception/
- [X] T073 [US4] Complete request ID response/header propagation in backend/src/main/java/com/cliniqo/config/RequestIdFilter.java
- [X] T074 [US4] Complete PHI-safe log redaction and MDC cleanup in backend/src/main/java/com/cliniqo/common/logging/PhiRedactionConverter.java and backend/src/main/resources/logback-spring.xml
- [X] T075 [US4] Add validation annotations and safe messages to auth and tenant DTOs in backend/src/main/java/com/cliniqo/auth/dto/ and backend/src/main/java/com/cliniqo/clinic/dto/
- [X] T076 [US4] Run and fix US4 envelope, request ID, exception mapping, and logging tests in backend/src/test/java/com/cliniqo/common/

**Checkpoint**: All F01 user stories are independently functional and validated.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final verification, documentation, and cleanup across the backend foundation.

- [X] T077 [P] Update backend validation notes in specs/001-foundation-auth-audit/quickstart.md
- [X] T078 [P] Add backend README with local run and test commands in backend/README.md
- [X] T079 Run full backend test suite and capture results in backend/README.md
- [X] T080 Verify OpenAPI contract matches implemented controllers in specs/001-foundation-auth-audit/contracts/foundation-api.openapi.yaml
- [X] T081 Verify no frontend files were created for F01 scope in frontend/
- [X] T082 Verify no appointment, WhatsApp, OpenAI, analytics, onboarding UI, or F17 support workflows were implemented in backend/src/main/java/com/cliniqo/
- [X] T083 Run quickstart validation scenarios from specs/001-foundation-auth-audit/quickstart.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Setup completion and blocks all user stories.
- **User Story 1 (Phase 3)**: Depends on Foundational; delivers authentication/session MVP.
- **User Story 2 (Phase 4)**: Depends on Foundational and uses US1 tokens for full integration tests.
- **User Story 3 (Phase 5)**: Depends on Foundational and integrates with US1/US2 event sources.
- **User Story 4 (Phase 6)**: Depends on Foundational and can run after or alongside US1/US2/US3 once shared response primitives exist.
- **Polish (Phase 7)**: Depends on all desired user stories.

### User Story Dependencies

- **US1 Authenticate Clinic Users Safely**: MVP first; required for authenticated integration flows.
- **US2 Enforce Clinic Tenant Isolation**: Can start after foundation; full integration verification benefits from US1 tokens.
- **US3 Record Auditable Foundation Activity**: Can start after foundation; complete coverage integrates with US1 and US2 events.
- **US4 Return Consistent Safe Service Errors**: Can start after foundation; final coverage spans all story flows.

### Within Each User Story

- Write the listed tests first and confirm they fail.
- Implement entities/DTOs before services.
- Implement services before controllers.
- Wire audit, request ID, redaction, and tenant context before declaring the story complete.
- Run the story-specific test directory before moving to the next checkpoint.

### Parallel Opportunities

- T003-T009 can run in parallel after backend directories exist.
- T012-T016, T018-T023, T025-T029, and T031-T033 can run in parallel during Phase 2.
- US1 tests T034-T037 can run in parallel.
- US2 tests T045-T049 can run in parallel.
- US3 tests T058-T060 can run in parallel.
- US4 tests T067-T070 can run in parallel.
- Implementation tasks marked [P] in each story can run in parallel once their phase begins.

---

## Parallel Example: User Story 1

```bash
# Contract and integration tests can be authored together:
Task: "T034 [P] [US1] Create contract tests in backend/src/test/java/com/cliniqo/auth/AuthContractTest.java"
Task: "T036 [P] [US1] Create refresh session tests in backend/src/test/java/com/cliniqo/auth/RefreshSessionIT.java"

# Independent services can be implemented together:
Task: "T038 [P] [US1] Implement backend/src/main/java/com/cliniqo/auth/service/CredentialService.java"
Task: "T039 [P] [US1] Implement backend/src/main/java/com/cliniqo/auth/service/RefreshSessionService.java"
```

## Parallel Example: User Story 2

```bash
Task: "T047 [P] [US2] Create cross-tenant tests in backend/src/test/java/com/cliniqo/tenancy/CrossTenantIsolationIT.java"
Task: "T049 [P] [US2] Create architecture tests in backend/src/test/java/com/cliniqo/tenancy/TenancyArchitectureTest.java"
Task: "T050 [P] [US2] Implement backend/src/main/java/com/cliniqo/clinic/context/TenantTamperDetector.java"
Task: "T051 [P] [US2] Implement backend/src/main/java/com/cliniqo/common/repository/TenantScopedRepositorySupport.java"
```

## Parallel Example: User Story 3

```bash
Task: "T058 [P] [US3] Create audit transaction tests in backend/src/test/java/com/cliniqo/audit/AuditTransactionIT.java"
Task: "T059 [P] [US3] Create audit redaction tests in backend/src/test/java/com/cliniqo/audit/AuditRedactionIT.java"
Task: "T061 [P] [US3] Implement backend/src/main/java/com/cliniqo/audit/service/AuditMetadataRedactor.java"
Task: "T062 [P] [US3] Implement backend/src/main/java/com/cliniqo/audit/dto/"
```

## Parallel Example: User Story 4

```bash
Task: "T067 [P] [US4] Create envelope tests in backend/src/test/java/com/cliniqo/common/ApiEnvelopeContractTest.java"
Task: "T069 [P] [US4] Create PHI logging tests in backend/src/test/java/com/cliniqo/common/PhiSafeLoggingIT.java"
Task: "T071 [P] [US4] Finalize backend/src/main/java/com/cliniqo/common/dto/"
Task: "T072 [P] [US4] Finalize backend/src/main/java/com/cliniqo/common/exception/"
```

---

## Implementation Strategy

### MVP First

1. Complete Phase 1 and Phase 2.
2. Complete Phase 3 / US1 authentication and refresh sessions.
3. Validate login, refresh rotation, logout, and revoked-session behavior before using auth in later stories.

### Incremental Delivery

1. Foundation scaffolding.
2. US1 authentication/session.
3. US2 tenant isolation and SUPER_ADMIN route context placeholder.
4. US3 audit transactionality.
5. US4 envelope, errors, request IDs, and PHI-safe logs.
6. Polish and quickstart validation.

### Team Parallel Strategy

After Phase 2, one developer can own US1 auth, another can own US2 tenant isolation, another can own US3 audit, and another can own US4 response/logging. Coordinate on shared DTOs, exception mapping, and audit event categories before merging.

## Notes

- F01 is backend-only; do not create frontend auth shell files.
- F01 creates SUPER_ADMIN route-context placeholders only; actual cross-clinic support data access remains F17.
- Java target is 21 per plan/research despite the Java 25 conflict in TECH_STACK.md.
- Every task must preserve PHI-safe logging and tenant-safe 404 behavior.
