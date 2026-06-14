package com.cliniqo.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cliniqo.audit.service.AuditMetadataRedactor;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ClinicDeactivationAuditIT {
    @Test
    void redactsPhoneInAuditMetadata() {
        assertEquals("[REDACTED]", new AuditMetadataRedactor().redact(Map.of("phone", "+123")).get("phone"));
    }
}
