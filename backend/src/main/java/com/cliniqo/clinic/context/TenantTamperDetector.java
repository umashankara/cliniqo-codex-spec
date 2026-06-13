package com.cliniqo.clinic.context;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TenantTamperDetector {
    public boolean containsTenantHint(Map<String, ?> payload) {
        if (payload == null || payload.isEmpty()) {
            return false;
        }
        return payload.keySet().stream().anyMatch(this::isTenantKey);
    }

    public boolean isTenantKey(String key) {
        return key != null && (
                key.equalsIgnoreCase("clinicId") ||
                key.equalsIgnoreCase("clinic_id") ||
                key.equalsIgnoreCase("tenantId") ||
                key.equalsIgnoreCase("tenant_id"));
    }
}
