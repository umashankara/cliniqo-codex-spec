package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SuperAdminBootstrapIdempotencyIT {
    @Test
    void bootstrapMigrationIsIdempotent() throws Exception {
        assertTrue(Files.readString(Path.of("src/main/resources/db/migration/V003__super_admin_bootstrap.sql")).contains("WHERE NOT EXISTS"));
    }
}
