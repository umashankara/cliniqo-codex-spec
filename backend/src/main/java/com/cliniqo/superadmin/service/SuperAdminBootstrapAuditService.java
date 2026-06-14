package com.cliniqo.superadmin.service;

import com.cliniqo.audit.service.AuditService;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.security.UserPrincipal;
import com.cliniqo.config.RequestIdFilter;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminBootstrapAuditService {
    private final AuditService auditService;

    public SuperAdminBootstrapAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    public void available(UserPrincipal actor, long count) {
        auditService.record(AuditEventType.BOOTSTRAP_SUPER_ADMIN_AVAILABLE, actor.getUserId(), actor.getRole(), null,
                "User", actor.getUserId(), RequestIdFilter.currentRequestId(), Map.of("activeSuperAdminCount", count));
    }
}
