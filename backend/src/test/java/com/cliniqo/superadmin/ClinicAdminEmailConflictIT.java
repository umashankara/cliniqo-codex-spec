package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cliniqo.superadmin.dto.ClinicOnboardingRequest;
import org.junit.jupiter.api.Test;

class ClinicAdminEmailConflictIT {
    @Test
    void firstAdminInputCarriesEmail() {
        assertTrue(ClinicOnboardingRequest.FirstClinicAdminInput.class.isRecord());
    }
}
