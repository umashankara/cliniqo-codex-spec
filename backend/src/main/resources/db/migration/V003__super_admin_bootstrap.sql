INSERT INTO users (id, clinic_id, email, password_hash, role, status, force_password_reset)
SELECT gen_random_uuid(), NULL, '${bootstrapSuperAdminEmail}', '${bootstrapSuperAdminPasswordHash}', 'SUPER_ADMIN', 'ACTIVE', false
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE role = 'SUPER_ADMIN' AND status = 'ACTIVE' AND deleted_at IS NULL
);
