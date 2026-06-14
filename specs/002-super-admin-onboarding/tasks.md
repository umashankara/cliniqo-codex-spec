# Tasks: F02 Super Admin Bootstrap and Clinic Onboarding

**Input**: Design documents from `/specs/002-super-admin-onboarding/`

**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `quickstart.md`, `contracts/super-admin-onboarding-api.openapi.yaml`, `contracts/onboarding-ui.md`

**Tests**: Required by the Cliniqo constitution because F02 touches SUPER_ADMIN safeguards, tenant boundaries, auth refresh security, audit, PHI/secret redaction, concurrency, idempotency, and minimal UI behavior.

**Organization**: Tasks are grouped by user story so each story can be implemented and tested as an independent increment after shared foundations are complete.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel when different files are touched and no unfinished task dependency exists
- **[Story]**: User story label for story phases only (`US1` through `US5`)
- Every task includes an exact target file path or directory path

## Path Conventions

- **Backend source**: `backend/src/main/java/com/cliniqo/`
- **Backend migrations**: `backend/src/main/resources/db/migration/`
- **Backend tests**: `backend/src/test/java/com/cliniqo/`
- **Backend test resources**: `backend/src/test/resources/`
- **Frontend source**: `frontend/src/`
- **Frontend tests**: `frontend/src/**/*.test.tsx`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Prepare project structure, contracts, and shared build files needed before feature work.

- [ ] T001 Copy F02 OpenAPI contract into backend test resources at `backend/src/test/resources/contracts/super-admin-onboarding-api.openapi.yaml`
- [ ] T002 [P] Create backend F02 package directories under `backend/src/main/java/com/cliniqo/superadmin/`, `backend/src/main/java/com/cliniqo/clinic/`, `backend/src/main/java/com/cliniqo/whatsapp/`, `backend/src/main/java/com/cliniqo/publicwebsite/`, `backend/src/main/java/com/cliniqo/notification/`, and `backend/src/main/java/com/cliniqo/common/crypto/`
- [ ] T003 [P] Create backend F02 test package directories under `backend/src/test/java/com/cliniqo/superadmin/`, `backend/src/test/java/com/cliniqo/clinic/`, `backend/src/test/java/com/cliniqo/whatsapp/`, `backend/src/test/java/com/cliniqo/auth/`, `backend/src/test/java/com/cliniqo/audit/`, and `backend/src/test/java/com/cliniqo/support/`
- [ ] T004 [P] Create minimal React/Vite frontend project skeleton in `frontend/package.json`, `frontend/vite.config.ts`, `frontend/tsconfig.json`, `frontend/tailwind.config.js`, `frontend/postcss.config.js`, and `frontend/src/`
- [ ] T005 [P] Add frontend test setup in `frontend/src/test/setup.ts` and `frontend/src/test/test-utils.tsx`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Shared backend and frontend primitives that must exist before story implementation begins.

**Critical**: No user story work should begin until this phase is complete.

- [ ] T006 Add F02 audit event constants to `backend/src/main/java/com/cliniqo/common/enums/AuditEventType.java`
- [ ] T007 Add F02 typed error codes including `CLINIC_INACTIVE` and `IDEMPOTENCY_CONFLICT` to `backend/src/main/java/com/cliniqo/common/enums/ErrorCode.java`
- [ ] T008 Add F02 role/status enum values needed for onboarding and first-login reset in `backend/src/main/java/com/cliniqo/common/enums/UserRole.java` and `backend/src/main/java/com/cliniqo/common/enums/UserStatus.java`
- [ ] T009 [P] Implement AES-based string encryption utility for WhatsApp credential placeholders in `backend/src/main/java/com/cliniqo/common/crypto/CredentialEncryptor.java`
- [ ] T010 [P] Add encryption configuration properties with required non-test validation in `backend/src/main/java/com/cliniqo/config/CredentialEncryptionProperties.java`
- [ ] T011 [P] Add bootstrap SUPER_ADMIN configuration properties and non-test missing/invalid placeholder validation in `backend/src/main/java/com/cliniqo/config/BootstrapSuperAdminProperties.java`
- [ ] T012 [P] Add slug validation utility that rejects non-canonical lower-case URL-safe slugs in `backend/src/main/java/com/cliniqo/common/validation/SlugValidator.java`
- [ ] T013 [P] Add timezone, default-language, and operating-hours validation helpers in `backend/src/main/java/com/cliniqo/clinic/service/ClinicOnboardingValidationService.java`
- [ ] T014 [P] Add idempotency key and request fingerprint value objects in `backend/src/main/java/com/cliniqo/common/idempotency/IdempotencyKey.java` and `backend/src/main/java/com/cliniqo/common/idempotency/RequestFingerprint.java`
- [ ] T015 Add shared idempotency repository support in `backend/src/main/java/com/cliniqo/superadmin/repository/OnboardingRequestRecordRepository.java`
- [ ] T016 Add shared idempotency service for onboarding and deactivation retries in `backend/src/main/java/com/cliniqo/superadmin/service/SuperAdminIdempotencyService.java`
- [ ] T017 Update audit metadata redaction for email, phone, token, password, WABA, and placeholder fields in `backend/src/main/java/com/cliniqo/audit/service/AuditMetadataRedactor.java`
- [ ] T018 Update log redaction patterns for temporary passwords and WhatsApp placeholders in `backend/src/main/resources/logback-spring.xml`
- [ ] T019 [P] Add SUPER_ADMIN authorization helper for platform-only APIs in `backend/src/main/java/com/cliniqo/superadmin/service/SuperAdminAuthorizationService.java`
- [ ] T020 [P] Add F02 test fixtures for users, JWTs, clinics, refresh sessions, and onboarding payloads in `backend/src/test/java/com/cliniqo/support/F02Fixtures.java`

**Checkpoint**: Shared F02 primitives are available; user story implementation can proceed.

---

## Phase 3: User Story 1 - Bootstrap Platform Administration (Priority: P1) - MVP

**Goal**: Provision exactly one configured active SUPER_ADMIN via Flyway, ensure SUPER_ADMIN sessions have no clinic scope, and block non-SUPER_ADMIN platform actions before state changes.

**Independent Test**: From an empty migrated database, verify one configured SUPER_ADMIN can sign in, repeated setup creates no duplicate, the token has no clinic scope, and non-SUPER_ADMIN users receive safe forbidden responses for platform APIs.

### Tests for User Story 1

- [ ] T021 [P] [US1] Add Flyway bootstrap placeholder integration test for configured email/hash plus missing/invalid non-test placeholders in `backend/src/test/java/com/cliniqo/superadmin/SuperAdminBootstrapFlywayIT.java`
- [ ] T022 [P] [US1] Add bootstrap idempotency test for repeated migrations in `backend/src/test/java/com/cliniqo/superadmin/SuperAdminBootstrapIdempotencyIT.java`
- [ ] T023 [P] [US1] Add SUPER_ADMIN no-clinic-scope session test in `backend/src/test/java/com/cliniqo/superadmin/SuperAdminSessionScopeIT.java`
- [ ] T024 [P] [US1] Add SUPER_ADMIN-only API authorization contract test in `backend/src/test/java/com/cliniqo/superadmin/SuperAdminAuthorizationIT.java`
- [ ] T025 [P] [US1] Add Clinic Admin cannot create or elevate SUPER_ADMIN test in `backend/src/test/java/com/cliniqo/superadmin/ClinicUserSuperAdminElevationIT.java`
- [ ] T026 [P] [US1] Add bootstrap secret redaction test in `backend/src/test/java/com/cliniqo/superadmin/SuperAdminBootstrapSecretSafetyIT.java`

### Implementation for User Story 1

- [ ] T027 [US1] Add Flyway migration for bootstrap SUPER_ADMIN placeholders and idempotent insert in `backend/src/main/resources/db/migration/V003__super_admin_bootstrap.sql`
- [ ] T028 [US1] Add documented non-live local/test bootstrap hash properties in `backend/src/main/resources/application.yml`
- [ ] T029 [US1] Update user repository queries for active SUPER_ADMIN availability in `backend/src/main/java/com/cliniqo/auth/repository/UserRepository.java`
- [ ] T030 [US1] Implement bootstrap status service in `backend/src/main/java/com/cliniqo/superadmin/service/SuperAdminBootstrapService.java`
- [ ] T031 [US1] Implement `GET /api/v1/super-admin/bootstrap/status` in `backend/src/main/java/com/cliniqo/superadmin/controller/SuperAdminBootstrapController.java`
- [ ] T032 [US1] Write bootstrap and protected-action audit events in `backend/src/main/java/com/cliniqo/superadmin/service/SuperAdminBootstrapAuditService.java`

**Checkpoint**: User Story 1 is fully functional and testable independently.

---

## Phase 4: User Story 2 - Onboard a Clinic Atomically (Priority: P1)

**Goal**: Allow a SUPER_ADMIN to create a clinic and all required defaults, WhatsApp metadata, first Clinic Admin, permissions, one-time credential, and audit events in one all-or-nothing transaction.

**Independent Test**: Submit one valid onboarding request and verify all required records exist together; then force a required write or audit failure and verify no partial state remains.

### Tests for User Story 2

- [ ] T033 [P] [US2] Add OpenAPI contract test for `POST /api/v1/super-admin/onboarding/clinics` success shape, including sensitive one-time `temporaryPassword` response semantics, in `backend/src/test/java/com/cliniqo/superadmin/ClinicOnboardingContractTest.java`
- [ ] T034 [P] [US2] Add atomic successful onboarding integration test in `backend/src/test/java/com/cliniqo/superadmin/ClinicOnboardingSuccessIT.java`
- [ ] T035 [P] [US2] Add onboarding rollback integration test for child-write and audit-write failures in `backend/src/test/java/com/cliniqo/superadmin/ClinicOnboardingRollbackIT.java`
- [ ] T036 [P] [US2] Add one-time temporary password response test in `backend/src/test/java/com/cliniqo/superadmin/TemporaryCredentialOneTimeIT.java`
- [ ] T037 [P] [US2] Add forced first-login reset test for the created Clinic Admin in `backend/src/test/java/com/cliniqo/auth/FirstLoginPasswordResetIT.java`
- [ ] T038 [P] [US2] Add WhatsApp credential encryption persistence test in `backend/src/test/java/com/cliniqo/whatsapp/WhatsAppCredentialEncryptionIT.java`
- [ ] T039 [P] [US2] Add PHI/secret-safe audit metadata test for onboarding in `backend/src/test/java/com/cliniqo/audit/OnboardingAuditRedactionIT.java`
- [ ] T040 [P] [US2] Add idempotent onboarding replay test proving no duplicate credential is generated in `backend/src/test/java/com/cliniqo/superadmin/ClinicOnboardingIdempotencyIT.java`

### Implementation for User Story 2

- [ ] T041 [US2] Add Flyway migration for clinic onboarding tables, constraints, and indexes in `backend/src/main/resources/db/migration/V004__clinic_onboarding_schema.sql`
- [ ] T042 [P] [US2] Expand clinic entity fields and status handling in `backend/src/main/java/com/cliniqo/clinic/entity/Clinic.java`
- [ ] T043 [P] [US2] Create clinic settings, operating hours, reminder defaults, and FAQ seed entities in `backend/src/main/java/com/cliniqo/clinic/entity/`
- [ ] T044 [P] [US2] Create public website slug reservation entity in `backend/src/main/java/com/cliniqo/publicwebsite/entity/PublicWebsiteSlugReservation.java`
- [ ] T045 [P] [US2] Create WhatsApp metadata entity with encrypted fields in `backend/src/main/java/com/cliniqo/whatsapp/entity/WhatsAppMetadata.java`
- [ ] T046 [P] [US2] Create default permission assignment entity in `backend/src/main/java/com/cliniqo/auth/entity/DefaultPermissionAssignment.java`
- [ ] T047 [P] [US2] Create onboarding idempotency entity in `backend/src/main/java/com/cliniqo/superadmin/entity/OnboardingRequestRecord.java`
- [ ] T048 [P] [US2] Create repositories for clinic settings, operating hours, reminder defaults, FAQ seeds, public slug reservations, WhatsApp metadata, and permissions in `backend/src/main/java/com/cliniqo/`
- [ ] T049 [P] [US2] Create onboarding request/response DTOs matching the OpenAPI contract in `backend/src/main/java/com/cliniqo/superadmin/dto/`
- [ ] T050 [P] [US2] Create onboarding mapper for DTO-to-domain assembly in `backend/src/main/java/com/cliniqo/superadmin/mapper/ClinicOnboardingMapper.java`
- [ ] T051 [US2] Implement default FAQ seed provider in `backend/src/main/java/com/cliniqo/clinic/service/DefaultFaqSeedProvider.java`
- [ ] T052 [US2] Implement temporary password generation and one-time result handling in `backend/src/main/java/com/cliniqo/auth/service/TemporaryCredentialService.java`
- [ ] T053 [US2] Implement first Clinic Admin creation and default permission assignment in `backend/src/main/java/com/cliniqo/auth/service/ClinicAdminProvisioningService.java`
- [ ] T054 [US2] Implement transactional clinic onboarding orchestration in `backend/src/main/java/com/cliniqo/superadmin/service/ClinicOnboardingService.java`
- [ ] T055 [US2] Implement onboarding audit publisher in `backend/src/main/java/com/cliniqo/superadmin/service/ClinicOnboardingAuditService.java`
- [ ] T056 [US2] Implement `POST /api/v1/super-admin/onboarding/clinics` in `backend/src/main/java/com/cliniqo/superadmin/controller/ClinicOnboardingController.java`

**Checkpoint**: User Story 2 is fully functional and testable independently with US1 authorization.

---

## Phase 5: User Story 3 - Prevent Duplicate Clinic Identity and Channels (Priority: P1)

**Goal**: Return safe field-specific conflicts for duplicate clinic slug, first-admin email, WhatsApp display phone number, and WhatsApp phone number ID, including concurrent submissions.

**Independent Test**: Submit duplicate and concurrent onboarding requests and verify only one succeeds, all failures are field-specific, and no partial onboarding state or temporary credential is created for rejected attempts.

### Tests for User Story 3

- [ ] T057 [P] [US3] Add duplicate clinic slug conflict test in `backend/src/test/java/com/cliniqo/superadmin/ClinicSlugConflictIT.java`
- [ ] T058 [P] [US3] Add duplicate public website slug conflict test in `backend/src/test/java/com/cliniqo/superadmin/PublicWebsiteSlugConflictIT.java`
- [ ] T059 [P] [US3] Add duplicate first Clinic Admin email conflict test in `backend/src/test/java/com/cliniqo/superadmin/ClinicAdminEmailConflictIT.java`
- [ ] T060 [P] [US3] Add duplicate WhatsApp display phone conflict test in `backend/src/test/java/com/cliniqo/whatsapp/WhatsAppDisplayPhoneConflictIT.java`
- [ ] T061 [P] [US3] Add duplicate WhatsApp phone number ID conflict test in `backend/src/test/java/com/cliniqo/whatsapp/WhatsAppPhoneNumberIdConflictIT.java`
- [ ] T062 [P] [US3] Add concurrent duplicate submission test in `backend/src/test/java/com/cliniqo/superadmin/ClinicOnboardingConcurrencyIT.java`
- [ ] T063 [P] [US3] Add non-canonical slug rejection test in `backend/src/test/java/com/cliniqo/superadmin/SlugCanonicalizationIT.java`

### Implementation for User Story 3

- [ ] T064 [US3] Add repository existence and lookup methods for slug/email/WhatsApp uniqueness in `backend/src/main/java/com/cliniqo/clinic/repository/ClinicRepository.java`, `backend/src/main/java/com/cliniqo/auth/repository/UserRepository.java`, and `backend/src/main/java/com/cliniqo/whatsapp/repository/WhatsAppMetadataRepository.java`
- [ ] T065 [US3] Implement preflight uniqueness validation and non-canonical slug rejection in `backend/src/main/java/com/cliniqo/superadmin/service/ClinicOnboardingConflictService.java`
- [ ] T066 [US3] Map database constraint violations to field-specific `409 CONFLICT` errors in `backend/src/main/java/com/cliniqo/common/exception/GlobalExceptionHandler.java`

**Checkpoint**: User Story 3 conflict behavior is fully functional and testable independently with US2 onboarding.

---

## Phase 6: User Story 4 - Deactivate a Clinic Safely (Priority: P2)

**Goal**: Allow SUPER_ADMIN to deactivate a clinic, revoke all clinic-user refresh sessions, reject later refresh with typed `401 CLINIC_INACTIVE`, and write safe audit events.

**Independent Test**: Deactivate an active clinic with signed-in users and verify the clinic is inactive, refresh sessions are revoked, affected refresh attempts fail with `CLINIC_INACTIVE`, and audit records contain actor, clinic, reason, request ID, and revocation summary without secrets.

### Tests for User Story 4

- [ ] T067 [P] [US4] Add OpenAPI contract test for `POST /api/v1/super-admin/clinics/{clinicId}/deactivate` in `backend/src/test/java/com/cliniqo/superadmin/ClinicDeactivationContractTest.java`
- [ ] T068 [P] [US4] Add clinic deactivation integration test in `backend/src/test/java/com/cliniqo/superadmin/ClinicDeactivationIT.java`
- [ ] T069 [P] [US4] Add clinic refresh-session revocation test in `backend/src/test/java/com/cliniqo/auth/ClinicDeactivationRefreshRevocationIT.java`
- [ ] T070 [P] [US4] Add `/auth/refresh` contract test for F01 success envelope reuse and inactive-clinic typed `401 CLINIC_INACTIVE` behavior in `backend/src/test/java/com/cliniqo/auth/InactiveClinicRefreshIT.java`
- [ ] T071 [P] [US4] Add repeat-safe deactivation idempotency test in `backend/src/test/java/com/cliniqo/superadmin/ClinicDeactivationIdempotencyIT.java`
- [ ] T072 [P] [US4] Add clinic deactivation audit redaction test in `backend/src/test/java/com/cliniqo/audit/ClinicDeactivationAuditIT.java`

### Implementation for User Story 4

- [ ] T073 [US4] Add clinic deactivation persistence fields if missing in `backend/src/main/resources/db/migration/V005__clinic_deactivation.sql`
- [ ] T074 [US4] Add refresh-session bulk revocation repository method by clinic user ownership in `backend/src/main/java/com/cliniqo/auth/repository/RefreshSessionRepository.java`
- [ ] T075 [US4] Implement clinic-user refresh-session revocation service in `backend/src/main/java/com/cliniqo/auth/service/ClinicRefreshSessionRevocationService.java`
- [ ] T076 [US4] Implement inactive-clinic refresh rejection in `backend/src/main/java/com/cliniqo/auth/service/AuthService.java`
- [ ] T077 [US4] Implement transactional clinic deactivation service in `backend/src/main/java/com/cliniqo/superadmin/service/ClinicDeactivationService.java`
- [ ] T078 [US4] Implement `POST /api/v1/super-admin/clinics/{clinicId}/deactivate` in `backend/src/main/java/com/cliniqo/superadmin/controller/ClinicDeactivationController.java`

**Checkpoint**: User Story 4 is fully functional and testable independently with an onboarded clinic.

---

## Phase 7: User Story 5 - Complete Onboarding from a Minimal Platform UI (Priority: P2)

**Goal**: Provide a focused SUPER_ADMIN onboarding screen with validation, safe errors, duplicate-submit protection, and one-time temporary credential display without creating a full platform dashboard.

**Independent Test**: Sign in as SUPER_ADMIN, open `/super-admin/onboarding`, submit valid and invalid payloads, verify loading/error/success states, verify duplicate conflicts map to fields, and verify temporary password is not persisted or recoverable after leaving the success state.

### Tests for User Story 5

- [ ] T079 [P] [US5] Add route guard tests for unauthenticated, non-SUPER_ADMIN, and SUPER_ADMIN users in `frontend/src/features/superadmin/onboarding/OnboardingRouteGuard.test.tsx`
- [ ] T080 [P] [US5] Add onboarding form validation tests for required fields, slug format, timezone/default language, ranges, and operating hours in `frontend/src/features/superadmin/onboarding/OnboardingForm.test.tsx`
- [ ] T081 [P] [US5] Add API client tests for onboarding success and duplicate-field errors in `frontend/src/services/superAdminOnboardingApi.test.ts`
- [ ] T082 [P] [US5] Add one-time credential display and no browser persistence tests in `frontend/src/features/superadmin/onboarding/OneTimeCredentialPanel.test.tsx`
- [ ] T083 [P] [US5] Add safe unexpected-error/request-ID state tests in `frontend/src/features/superadmin/onboarding/OnboardingErrorState.test.tsx`

### Implementation for User Story 5

- [ ] T084 [US5] Create frontend app entry and router with `/super-admin/onboarding` route in `frontend/src/app/App.tsx` and `frontend/src/main.tsx`
- [ ] T085 [US5] Create auth-aware SUPER_ADMIN route guard in `frontend/src/routes/SuperAdminRoute.tsx`
- [ ] T086 [P] [US5] Create onboarding API client and types in `frontend/src/services/superAdminOnboardingApi.ts` and `frontend/src/types/superAdminOnboarding.ts`
- [ ] T087 [P] [US5] Create Zod schemas for onboarding form validation in `frontend/src/features/superadmin/onboarding/onboardingSchema.ts`
- [ ] T088 [US5] Create onboarding page layout without full dashboard navigation in `frontend/src/features/superadmin/onboarding/OnboardingPage.tsx`
- [ ] T089 [P] [US5] Create clinic profile and operating defaults form sections in `frontend/src/features/superadmin/onboarding/ClinicProfileSection.tsx` and `frontend/src/features/superadmin/onboarding/OperatingDefaultsSection.tsx`
- [ ] T090 [P] [US5] Create public website, WhatsApp metadata, and first-admin form sections in `frontend/src/features/superadmin/onboarding/PublicWebsiteSection.tsx`, `frontend/src/features/superadmin/onboarding/WhatsAppMetadataSection.tsx`, and `frontend/src/features/superadmin/onboarding/FirstAdminSection.tsx`
- [ ] T091 [US5] Create submit orchestration with TanStack Query and duplicate-submit protection in `frontend/src/features/superadmin/onboarding/useSubmitOnboarding.ts`
- [ ] T092 [US5] Create field-specific backend error mapping in `frontend/src/features/superadmin/onboarding/onboardingErrorMapper.ts`
- [ ] T093 [US5] Create one-time temporary credential success panel that never persists secrets in `frontend/src/features/superadmin/onboarding/OneTimeCredentialPanel.tsx`
- [ ] T094 [US5] Create safe loading, permission-denied, validation, conflict, and unexpected-error states in `frontend/src/features/superadmin/onboarding/OnboardingStates.tsx`

**Checkpoint**: User Story 5 is fully functional and testable independently against the F02 backend contracts.

---

## Final Phase: Polish & Cross-Cutting Concerns

**Purpose**: Verify scope, documentation, performance, and cross-story quality after desired stories are complete.

- [ ] T095 [P] Run backend unit tests and fix F02 regressions in `backend/src/test/java/com/cliniqo/`
- [ ] T096 [P] Run backend PostgreSQL integration tests and fix F02 regressions in `backend/src/test/java/com/cliniqo/`
- [ ] T097 [P] Run frontend tests and production build and fix F02 regressions in `frontend/src/`
- [ ] T098 [P] Validate OpenAPI YAML remains parseable and aligned with controller DTOs in `specs/002-super-admin-onboarding/contracts/super-admin-onboarding-api.openapi.yaml`
- [ ] T099 Verify quickstart validation scenarios end-to-end and update notes only if behavior changed in `specs/002-super-admin-onboarding/quickstart.md`
- [ ] T100 Verify no full dashboard, appointment booking, public website pages, WhatsApp webhook/live send/template registration, support console, purge, or analytics behavior was added in `backend/src/main/java/com/cliniqo/` and `frontend/src/`
- [ ] T101 Verify no temporary passwords, password hashes, WhatsApp secrets, raw phone numbers, or tokens appear in logs, audit metadata, frontend storage, or repeatable API responses in `backend/src/test/java/com/cliniqo/` and `frontend/src/`
- [ ] T102 Verify performance targets for onboarding, duplicate conflicts, and deactivation using integration tests or local measurements in `backend/src/test/java/com/cliniqo/superadmin/`
- [ ] T103 [P] Update developer-facing implementation notes for bootstrap placeholders and local non-live hash in `specs/002-super-admin-onboarding/quickstart.md`
- [ ] T104 [P] Update API documentation examples for one-time temporary password and `CLINIC_INACTIVE` refresh errors in `specs/002-super-admin-onboarding/contracts/super-admin-onboarding-api.openapi.yaml`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 Setup**: No dependencies.
- **Phase 2 Foundational**: Depends on Phase 1 and blocks all user stories.
- **Phase 3 US1**: Depends on Phase 2.
- **Phase 4 US2**: Depends on Phase 2 and uses US1 authorization for protected execution.
- **Phase 5 US3**: Depends on US2 onboarding persistence and service flow.
- **Phase 6 US4**: Depends on US2 onboarded clinic/user/session data.
- **Phase 7 US5**: Depends on stable backend contracts from US1 through US3 for full success/conflict coverage; route guard can start after Phase 2.
- **Final Phase**: Depends on all selected user stories.

### User Story Dependencies

- **US1 (P1)**: Independent MVP foundation for platform access.
- **US2 (P1)**: Requires foundational services and should be implemented after or alongside US1 protected-route wiring.
- **US3 (P1)**: Requires US2 onboarding flow and persistence constraints.
- **US4 (P2)**: Requires clinic and user data from US2.
- **US5 (P2)**: Requires API contracts and can be developed with mocked services after Phase 2, then integrated once US1-US3 backend behavior is available.

### Within Each User Story

- Write tests first and confirm they fail before implementation.
- Implement migrations/entities before repositories and services.
- Implement services before controllers.
- Implement audit and redaction with the state-changing service, not as a later add-on.
- Complete independent story validation before moving to the next priority story.

## Parallel Opportunities

- Setup tasks T002-T005 can run in parallel.
- Foundational utility/config/test-fixture tasks T009-T014 and T019-T020 can run in parallel after enum/error decisions.
- US1 tests T021-T026 can run in parallel before US1 implementation.
- US2 tests T033-T040 can run in parallel; US2 entities/repositories T042-T050 can run in parallel after migration planning.
- US3 conflict tests T057-T063 can run in parallel.
- US4 tests T067-T072 can run in parallel.
- US5 frontend tests T079-T083 and component/API/schema tasks T086-T090 can run in parallel after the frontend skeleton exists.
- Final validation tasks T095-T098 and T103-T104 can run in parallel.

## Parallel Example: User Story 2

```bash
# Tests first:
Task: "T034 [P] [US2] Add atomic successful onboarding integration test in backend/src/test/java/com/cliniqo/superadmin/ClinicOnboardingSuccessIT.java"
Task: "T038 [P] [US2] Add WhatsApp credential encryption persistence test in backend/src/test/java/com/cliniqo/whatsapp/WhatsAppCredentialEncryptionIT.java"
Task: "T039 [P] [US2] Add PHI/secret-safe audit metadata test for onboarding in backend/src/test/java/com/cliniqo/audit/OnboardingAuditRedactionIT.java"

# Domain files after migration shape is agreed:
Task: "T043 [P] [US2] Create clinic settings, operating hours, reminder defaults, and FAQ seed entities in backend/src/main/java/com/cliniqo/clinic/entity/"
Task: "T045 [P] [US2] Create WhatsApp metadata entity with encrypted fields in backend/src/main/java/com/cliniqo/whatsapp/entity/WhatsAppMetadata.java"
Task: "T049 [P] [US2] Create onboarding request/response DTOs matching the OpenAPI contract in backend/src/main/java/com/cliniqo/superadmin/dto/"
```

## Parallel Example: User Story 5

```bash
# Frontend tests first:
Task: "T079 [P] [US5] Add route guard tests for unauthenticated, non-SUPER_ADMIN, and SUPER_ADMIN users in frontend/src/features/superadmin/onboarding/OnboardingRouteGuard.test.tsx"
Task: "T082 [P] [US5] Add one-time credential display and no browser persistence tests in frontend/src/features/superadmin/onboarding/OneTimeCredentialPanel.test.tsx"

# Components after frontend skeleton exists:
Task: "T087 [P] [US5] Create Zod schemas for onboarding form validation in frontend/src/features/superadmin/onboarding/onboardingSchema.ts"
Task: "T089 [P] [US5] Create clinic profile and operating defaults form sections in frontend/src/features/superadmin/onboarding/ClinicProfileSection.tsx and frontend/src/features/superadmin/onboarding/OperatingDefaultsSection.tsx"
Task: "T090 [P] [US5] Create public website, WhatsApp metadata, and first-admin form sections in frontend/src/features/superadmin/onboarding/PublicWebsiteSection.tsx, frontend/src/features/superadmin/onboarding/WhatsAppMetadataSection.tsx, and frontend/src/features/superadmin/onboarding/FirstAdminSection.tsx"
```

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 setup.
2. Complete Phase 2 foundational primitives.
3. Complete Phase 3 US1 bootstrap and platform authorization.
4. Stop and validate US1 independently: exactly one configured SUPER_ADMIN, no clinic scope, non-SUPER_ADMIN platform access denied.

### F02 Backend Core

1. Complete US2 atomic onboarding.
2. Complete US3 duplicate/conflict/concurrency hardening.
3. Complete US4 deactivation and refresh rejection.
4. Run backend quickstart validation before adding UI polish.

### Full F02 Delivery

1. Complete US5 minimal onboarding UI.
2. Run backend and frontend validation from `quickstart.md`.
3. Run final scope guardrails to confirm excluded features remain absent.
