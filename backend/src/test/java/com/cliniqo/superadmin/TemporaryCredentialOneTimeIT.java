package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cliniqo.superadmin.dto.ClinicOnboardingResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TemporaryCredentialOneTimeIT {
    @Test
    void responseModelsOneTimeCredential() {
        var response = new ClinicOnboardingResponse(UUID.randomUUID(), "care", "care", UUID.randomUUID(), "admin@example.com", "temp", true, true);
        assertTrue(response.oneTimeCredential());
    }
}
