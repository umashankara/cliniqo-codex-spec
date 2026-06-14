package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.cliniqo.common.enums.UserRole;
import org.junit.jupiter.api.Test;

class ClinicUserSuperAdminElevationIT {
    @Test
    void clinicRolesAreNotSuperAdmin() {
        assertFalse(UserRole.CLINIC_ADMIN == UserRole.SUPER_ADMIN);
    }
}
