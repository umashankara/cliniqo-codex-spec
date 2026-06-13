package com.cliniqo.audit.service;

import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.enums.UserRole;
import java.util.Map;
import java.util.UUID;

public interface AuditService {
    void record(AuditEventType eventType, UUID actorUserId, UserRole actorRole, UUID clinicId,
            String targetType, UUID targetId, String requestId, Map<String, Object> metadata);
}
