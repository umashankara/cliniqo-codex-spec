# UI Contract: Minimal SUPER_ADMIN Clinic Onboarding

## Route

- `/super-admin/onboarding`
- Requires authenticated `SUPER_ADMIN`.
- Redirects unauthenticated users to login.
- Shows safe permission-denied state for authenticated non-SUPER_ADMIN users.
- Does not expose full platform dashboard navigation in F02.

## Data Sources

- `POST /api/v1/super-admin/onboarding/clinics`
- `POST /api/v1/super-admin/clinics/{clinicId}/deactivate`
- Existing F01 auth endpoints for login, refresh, and logout.

## Form Sections

### Clinic Profile

- Name
- Slug
- Address
- Country
- Timezone
- Default language
- Primary phone
- Email
- Logo metadata fields if available

### Operating Defaults

- Slot duration
- Maximum advance booking days
- Minimum booking notice
- Cancellation cutoff
- Reschedule cutoff
- Weekly operating hours
- Reminder defaults: morning-of and before-appointment

### Public Website

- Public website slug
- Public website enabled flag
- Must clarify that no public page is exposed by F02.

### WhatsApp Metadata

- WABA ID
- Phone number ID
- Display phone number
- Template namespace
- Token/app-secret placeholder fields when supplied
- UI must never display stored secret-like values after submission.

### First Clinic Admin

- Full name
- Email
- Phone

## UI States

- Initial loading/auth-check state.
- Editable form state.
- Client validation error state.
- Backend validation error state.
- Duplicate-field conflict state for clinic slug, first-admin email, WhatsApp display phone number,
  and WhatsApp phone number ID.
- Submitting state with duplicate-submit protection.
- Success state with one-time temporary password display.
- Safe unexpected-error state with request ID.
- Permission-denied state for non-SUPER_ADMIN.

## One-Time Credential Rules

- Temporary password appears only in the immediate success state from onboarding.
- It must not be written to local storage, session storage, IndexedDB, URL parameters, console logs,
  analytics, telemetry, error messages, or persisted React Query cache.
- Refreshing, navigating away, logging out, or dismissing the success state loses the temporary
  password.
- The UI should tell the operator to hand over the credential now and use a reset flow if it is
  lost.

## Validation Rules

- Client-side validation mirrors required fields and obvious ranges for usability.
- Backend validation remains authoritative.
- Slug fields use lower-case URL-safe format.
- Slot duration allows 15, 20, 30, 45, or 60 minutes.
- Booking windows and cutoffs follow ranges documented in the API contract.
- Operating hour open time must be before close time for open days.

## PHI And Secret Safety

- Do not log request/response bodies.
- Do not display raw secret-like WhatsApp placeholders after submission.
- Do not persist temporary passwords.
- Error messages must be field-specific where possible and safe.
- Request IDs may be shown for support correlation.

## Out Of Scope

- Full SUPER_ADMIN dashboard.
- Clinic support console.
- Appointment booking.
- Public website pages.
- WhatsApp template registration.
- Webhook processing or message sending.
