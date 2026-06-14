package com.cliniqo.common.idempotency;

import java.util.UUID;

public record IdempotencyKey(String value) {
    public static IdempotencyKey of(String value) {
        if (value == null || value.isBlank()) {
            return new IdempotencyKey(UUID.randomUUID().toString());
        }
        return new IdempotencyKey(value.trim());
    }
}
