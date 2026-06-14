package com.cliniqo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BootstrapSuperAdminProperties {
    private final String email;
    private final String passwordHash;

    public BootstrapSuperAdminProperties(
            @Value("${cliniqo.bootstrap.super-admin-email}") String email,
            @Value("${cliniqo.bootstrap.super-admin-password-hash}") String passwordHash,
            Environment environment) {
        this.email = email;
        this.passwordHash = passwordHash;
        boolean testProfile = java.util.Arrays.asList(environment.getActiveProfiles()).contains("test")
                || java.util.Arrays.asList(environment.getActiveProfiles()).contains("local");
        if (!testProfile && (email == null || email.isBlank() || passwordHash == null || !passwordHash.startsWith("$2"))) {
            throw new IllegalStateException("Bootstrap SUPER_ADMIN email and bcrypt hash must be configured");
        }
    }

    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
}
