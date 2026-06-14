package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SuperAdminAuthorizationIT {
    @Test
    void securityConfigProtectsSuperAdminRoutes() throws Exception {
        assertTrue(Files.readString(Path.of("src/main/java/com/cliniqo/config/SecurityConfig.java")).contains("/api/v1/super-admin/**"));
    }
}
