CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE clinics (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(160) NOT NULL,
    slug varchar(120) NOT NULL UNIQUE,
    status varchar(32) NOT NULL DEFAULT 'ACTIVE',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid
);

CREATE TABLE users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id uuid REFERENCES clinics(id),
    email varchar(255) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    role varchar(32) NOT NULL,
    status varchar(32) NOT NULL DEFAULT 'ACTIVE',
    force_password_reset boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid,
    deleted_at timestamptz,
    deleted_by uuid,
    CONSTRAINT users_clinic_role_chk CHECK (
        (role = 'SUPER_ADMIN' AND clinic_id IS NULL) OR
        (role <> 'SUPER_ADMIN' AND clinic_id IS NOT NULL)
    )
);

CREATE INDEX idx_users_clinic_id ON users(clinic_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_active_email ON users(email) WHERE deleted_at IS NULL;

CREATE TABLE refresh_sessions (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users(id),
    token_hash varchar(128) NOT NULL UNIQUE,
    family_id uuid NOT NULL,
    issued_at timestamptz NOT NULL DEFAULT now(),
    expires_at timestamptz NOT NULL,
    last_used_at timestamptz,
    revoked_at timestamptz,
    revoked_reason varchar(80),
    replaced_by_session_id uuid REFERENCES refresh_sessions(id),
    request_id varchar(120),
    created_ip_hash varchar(128),
    user_agent_hash varchar(128),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid
);

CREATE INDEX idx_refresh_sessions_user ON refresh_sessions(user_id);
CREATE INDEX idx_refresh_sessions_family ON refresh_sessions(family_id);

CREATE TABLE audit_events (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type varchar(80) NOT NULL,
    actor_user_id uuid,
    actor_role varchar(32),
    clinic_id uuid,
    target_type varchar(80),
    target_id uuid,
    request_id varchar(120),
    metadata jsonb NOT NULL DEFAULT '{}'::jsonb,
    occurred_at timestamptz NOT NULL DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid
);

CREATE INDEX idx_audit_events_clinic ON audit_events(clinic_id, occurred_at DESC);
CREATE INDEX idx_audit_events_actor ON audit_events(actor_user_id, occurred_at DESC);
CREATE INDEX idx_audit_events_request ON audit_events(request_id);

CREATE TABLE tenant_probe_records (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id uuid NOT NULL REFERENCES clinics(id),
    label varchar(160) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_by uuid,
    updated_by uuid,
    deleted_at timestamptz,
    deleted_by uuid
);

CREATE INDEX idx_tenant_probe_records_clinic ON tenant_probe_records(clinic_id) WHERE deleted_at IS NULL;
