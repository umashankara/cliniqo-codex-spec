package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.cliniqo.common.enums.UserRole;
import com.cliniqo.common.security.UserPrincipal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SuperAdminSessionScopeIT {
    @Test
    void superAdminPrincipalHasNoClinicScope() {
        UserPrincipal principal = new UserPrincipal(UUID.randomUUID(), null, "super@cliniqo.test", "hash", UserRole.SUPER_ADMIN, true);
        assertNull(principal.getClinicId());
    }
}
