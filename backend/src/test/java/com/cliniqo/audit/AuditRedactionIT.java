package com.cliniqo.audit;

import static org.assertj.core.api.Assertions.assertThat;

import com.cliniqo.audit.service.AuditMetadataRedactor;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AuditRedactionIT {
    @Test
    void redactsSensitiveMetadataKeys() {
        AuditMetadataRedactor redactor = new AuditMetadataRedactor();
        Map<String, Object> redacted = redactor.redact(Map.of(
                "password", "secret",
                "refreshToken", "raw",
                "phone", "+15551234567",
                "safe", "ok"));
        assertThat(redacted.get("password")).isEqualTo("[REDACTED]");
        assertThat(redacted.get("refreshToken")).isEqualTo("[REDACTED]");
        assertThat(redacted.get("phone")).isEqualTo("[REDACTED]");
        assertThat(redacted.get("safe")).isEqualTo("ok");
    }
}
