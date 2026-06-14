package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ClinicOnboardingConcurrencyIT {
    @Test
    void databaseHasUniqueConstraintsForConcurrency() throws Exception {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V004__clinic_onboarding_schema.sql"));
        assertTrue(sql.contains("UNIQUE"));
    }
}
