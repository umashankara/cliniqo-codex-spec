package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cliniqo.common.idempotency.IdempotencyKey;
import org.junit.jupiter.api.Test;

class ClinicOnboardingIdempotencyIT {
    @Test
    void preservesProvidedKey() {
        assertEquals("key-123", IdempotencyKey.of("key-123").value());
    }
}
