package com.cliniqo.audit.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AuditMetadataRedactor {
    private static final String REDACTED = "[REDACTED]";
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password", "accessToken", "refreshToken", "token", "passwordHash",
            "apiKey", "appSecret", "verifyToken", "phone", "primaryPhone", "displayPhoneNumber",
            "temporaryPassword", "wabaId", "phoneNumberId", "accessTokenPlaceholder",
            "appSecretPlaceholder", "messageBody", "email");

    public Map<String, Object> redact(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> redacted = new LinkedHashMap<>();
        metadata.forEach((key, value) -> {
            if (key != null && SENSITIVE_KEYS.contains(key)) {
                redacted.put(key, REDACTED);
            } else {
                redacted.put(key, value);
            }
        });
        return redacted;
    }
}
