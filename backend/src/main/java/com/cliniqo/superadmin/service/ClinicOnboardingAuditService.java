package com.cliniqo.superadmin.service;

import com.cliniqo.audit.service.AuditService;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.security.UserPrincipal;
import com.cliniqo.config.RequestIdFilter;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ClinicOnboardingAuditService {
    private final AuditService auditService;

    public ClinicOnboardingAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    public void record(UserPrincipal actor, AuditEventType type, UUID clinicId, String targetType, UUID targetId, Map<String, Object> metadata) {
        auditService.record(type, actor.getUserId(), actor.getRole(), clinicId, targetType, targetId,
                RequestIdFilter.currentRequestId(), metadata == null ? Map.of() : metadata);
    }
}
