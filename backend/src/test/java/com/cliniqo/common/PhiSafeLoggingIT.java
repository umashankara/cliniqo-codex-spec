package com.cliniqo.common;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.cliniqo.common.logging.PhiRedactionConverter;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class PhiSafeLoggingIT {
    @Test
    void converterClassIsAvailableForLogback() {
        assertThat(PhiRedactionConverter.class.getName()).isEqualTo("com.cliniqo.common.logging.PhiRedactionConverter");
    }

    @Test
    void redactsCredentialsTokensContactDetailsPatientTextAndHashes() {
        TestablePhiRedactionConverter converter = new TestablePhiRedactionConverter();

        String redacted = converter.redact("""
                {"password":"Password123!","accessToken":"raw-access","refreshToken":"raw-refresh","apiKey":"raw-key","patientMessage":"patient has fever"}
                bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIn0.signature admin-a@cliniqo.test +15551234567 $2a$10$7EqJtq98hPqEX7fNZaFWoOhi4fdyE7v1pNJJ0jW5hDeAzbYfM84uS
                """);

        assertThat(redacted)
                .doesNotContain("Password123!")
                .doesNotContain("raw-access")
                .doesNotContain("raw-refresh")
                .doesNotContain("raw-key")
                .doesNotContain("patient has fever")
                .doesNotContain("eyJhbGciOiJIUzI1NiJ9")
                .doesNotContain("admin-a@cliniqo.test")
                .doesNotContain("+15551234567")
                .doesNotContain("$2a$10$7EqJtq98hPqEX7fNZaFWoOhi4fdyE7v1pNJJ0jW5hDeAzbYfM84uS")
                .contains("[REDACTED]");
    }

    @Test
    void localLogPatternDoesNotEmitExceptionStackTraceFields() throws Exception {
        String config = new ClassPathResource("logback-spring.xml")
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(config).contains("%redact(%msg)");
        assertThat(config).contains("LoggingEventCompositeJsonEncoder");
        assertThat(config).contains("\"message\":\"%redact(%msg)\"");
        assertThat(config)
                .doesNotContain("%ex")
                .doesNotContain("%exception")
                .doesNotContain("%throwable");
    }

    private static final class TestablePhiRedactionConverter extends PhiRedactionConverter {
        String redact(String message) {
            return transform((ILoggingEvent) null, message);
        }
    }
}
