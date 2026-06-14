package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SuperAdminBootstrapFlywayIT {
    @Test
    void migrationUsesConfiguredPlaceholders() throws Exception {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V003__super_admin_bootstrap.sql"));
        assertTrue(sql.contains("${bootstrapSuperAdminEmail}"));
        assertTrue(sql.contains("${bootstrapSuperAdminPasswordHash}"));
    }
}
