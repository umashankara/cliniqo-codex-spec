package com.cliniqo.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cliniqo.audit.service.AuditMetadataRedactor;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OnboardingAuditRedactionIT {
    @Test
    void redactsTemporaryPassword() {
        assertEquals("[REDACTED]", new AuditMetadataRedactor().redact(Map.of("temporaryPassword", "secret")).get("temporaryPassword"));
    }
}
