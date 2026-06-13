INSERT INTO clinics (id, name, slug, status)
VALUES
    ('00000000-0000-0000-0000-0000000000a1', 'Clinic A', 'clinic-a', 'ACTIVE'),
    ('00000000-0000-0000-0000-0000000000b1', 'Clinic B', 'clinic-b', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- BCrypt hash for fixture password: Password123!
INSERT INTO users (id, clinic_id, email, password_hash, role, status)
VALUES
    ('00000000-0000-0000-0000-000000000001', NULL, 'superadmin@cliniqo.test', '$2b$10$FBvZ3vIwO1z8bpqI7t.C9uHXdDs0b7kUSI4QVfIg2n/TlWrpm.Rhi', 'SUPER_ADMIN', 'ACTIVE'),
    ('00000000-0000-0000-0000-0000000000a2', '00000000-0000-0000-0000-0000000000a1', 'admin-a@cliniqo.test', '$2b$10$FBvZ3vIwO1z8bpqI7t.C9uHXdDs0b7kUSI4QVfIg2n/TlWrpm.Rhi', 'CLINIC_ADMIN', 'ACTIVE'),
    ('00000000-0000-0000-0000-0000000000b2', '00000000-0000-0000-0000-0000000000b1', 'admin-b@cliniqo.test', '$2b$10$FBvZ3vIwO1z8bpqI7t.C9uHXdDs0b7kUSI4QVfIg2n/TlWrpm.Rhi', 'CLINIC_ADMIN', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tenant_probe_records (id, clinic_id, label)
VALUES
    ('00000000-0000-0000-0000-0000000000a3', '00000000-0000-0000-0000-0000000000a1', 'Clinic A probe'),
    ('00000000-0000-0000-0000-0000000000b3', '00000000-0000-0000-0000-0000000000b1', 'Clinic B probe')
ON CONFLICT (id) DO NOTHING;
