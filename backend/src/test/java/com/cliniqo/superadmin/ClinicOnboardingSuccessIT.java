package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cliniqo.support.F02Fixtures;
import org.junit.jupiter.api.Test;

class ClinicOnboardingSuccessIT {
    @Test
    void fixtureContainsRequiredAtomicSections() {
        var request = F02Fixtures.onboardingRequest("care-clinic", "admin@example.com", "phone-id", "+919999999999");
        assertEquals("care-clinic", request.clinic().slug());
        assertEquals("phone-id", request.whatsapp().phoneNumberId());
    }
}
