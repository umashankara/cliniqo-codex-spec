package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SuperAdminBootstrapSecretSafetyIT {
    @Test
    void migrationDoesNotCommitPlaintextPassword() throws Exception {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V003__super_admin_bootstrap.sql")).toLowerCase();
        assertFalse(sql.contains("password123"));
    }
}
