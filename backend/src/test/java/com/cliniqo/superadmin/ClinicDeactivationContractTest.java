package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ClinicDeactivationContractTest {
    @Test
    void contractIncludesDeactivateEndpoint() throws Exception {
        assertTrue(Files.readString(Path.of("../specs/002-super-admin-onboarding/contracts/super-admin-onboarding-api.openapi.yaml")).contains("/super-admin/clinics/{clinicId}/deactivate"));
    }
}
