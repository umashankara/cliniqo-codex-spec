package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ClinicOnboardingContractTest {
    @Test
    void contractDocumentsOneTimeTemporaryPassword() throws Exception {
        String yaml = Files.readString(Path.of("../specs/002-super-admin-onboarding/contracts/super-admin-onboarding-api.openapi.yaml"));
        assertTrue(yaml.contains("temporaryPassword"));
        assertTrue(yaml.contains("x-one-time-secret"));
    }
}
