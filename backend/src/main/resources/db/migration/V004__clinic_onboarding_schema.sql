ALTER TABLE clinics
    ADD COLUMN IF NOT EXISTS address varchar(500),
    ADD COLUMN IF NOT EXISTS country varchar(80),
    ADD COLUMN IF NOT EXISTS timezone varchar(80) NOT NULL DEFAULT 'UTC',
    ADD COLUMN IF NOT EXISTS default_language varchar(16) NOT NULL DEFAULT 'en',
    ADD COLUMN IF NOT EXISTS primary_phone varchar(32),
    ADD COLUMN IF NOT EXISTS email varchar(255),
    ADD COLUMN IF NOT EXISTS logo_metadata text,
    ADD COLUMN IF NOT EXISTS deactivated_at timestamptz,
    ADD COLUMN IF NOT EXISTS deactivated_by uuid,
    ADD COLUMN IF NOT EXISTS deactivation_reason varchar(500);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS full_name varchar(160),
    ADD COLUMN IF NOT EXISTS phone varchar(32),
    ADD COLUMN IF NOT EXISTS temporary_credential_issued_at timestamptz;

CREATE TABLE clinic_settings (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id uuid NOT NULL UNIQUE REFERENCES clinics(id),
    slot_duration_minutes integer NOT NULL,
    max_advance_booking_days integer NOT NULL,
    min_booking_notice_minutes integer NOT NULL,
    cancellation_cutoff_minutes integer NOT NULL,
    reschedule_cutoff_minutes integer NOT NULL,
    default_language varchar(16) NOT NULL,
    public_website_enabled boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid
);

CREATE TABLE clinic_operating_hours (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id uuid NOT NULL REFERENCES clinics(id),
    day_of_week varchar(16) NOT NULL,
    open_time time,
    close_time time,
    closed boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid
);

CREATE TABLE reminder_defaults (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id uuid NOT NULL REFERENCES clinics(id),
    type varchar(32) NOT NULL,
    enabled boolean NOT NULL DEFAULT true,
    send_time_local time,
    offset_minutes integer,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid
);

CREATE TABLE faq_seeds (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id uuid NOT NULL REFERENCES clinics(id),
    category varchar(80) NOT NULL,
    question varchar(500) NOT NULL,
    answer varchar(1000) NOT NULL,
    enabled boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid
);

CREATE TABLE public_website_slug_reservations (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id uuid NOT NULL UNIQUE REFERENCES clinics(id),
    slug varchar(80) NOT NULL UNIQUE,
    enabled boolean NOT NULL DEFAULT false,
    reserved_at timestamptz NOT NULL DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid
);

CREATE TABLE whatsapp_metadata (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id uuid NOT NULL UNIQUE REFERENCES clinics(id),
    waba_id varchar(120) NOT NULL,
    phone_number_id varchar(120) NOT NULL UNIQUE,
    display_phone_number varchar(32) NOT NULL UNIQUE,
    template_namespace varchar(120) NOT NULL,
    encrypted_access_token_placeholder text,
    encrypted_app_secret_placeholder text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid
);

CREATE TABLE default_permission_assignments (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id uuid NOT NULL REFERENCES clinics(id),
    user_id uuid NOT NULL REFERENCES users(id),
    module varchar(80) NOT NULL,
    level varchar(32) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid,
    CONSTRAINT uq_default_permission UNIQUE (clinic_id, user_id, module)
);

CREATE TABLE onboarding_request_records (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key varchar(128) NOT NULL UNIQUE,
    request_fingerprint varchar(128) NOT NULL,
    actor_user_id uuid NOT NULL REFERENCES users(id),
    status varchar(32) NOT NULL,
    clinic_id uuid REFERENCES clinics(id),
    safe_result_summary text NOT NULL DEFAULT '{}',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    completed_at timestamptz
);

CREATE INDEX idx_clinic_settings_clinic ON clinic_settings(clinic_id);
CREATE INDEX idx_clinic_operating_hours_clinic ON clinic_operating_hours(clinic_id);
CREATE INDEX idx_reminder_defaults_clinic ON reminder_defaults(clinic_id);
CREATE INDEX idx_faq_seeds_clinic ON faq_seeds(clinic_id);
CREATE INDEX idx_onboarding_request_actor ON onboarding_request_records(actor_user_id);
