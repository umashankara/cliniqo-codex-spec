package com.cliniqo.audit.support;

import com.cliniqo.audit.service.AuditService;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.support.FoundationFixtures;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuditRollbackFixture {
    private final AuditService auditService;

    public AuditRollbackFixture(AuditService auditService) {
        this.auditService = auditService;
    }

    @Transactional
    public void writeThenRollback(String requestId) {
        auditService.record(AuditEventType.TEST_MUTATION, FoundationFixtures.CLINIC_A_ADMIN_ID,
                com.cliniqo.common.enums.UserRole.CLINIC_ADMIN, FoundationFixtures.CLINIC_A_ID,
                "TenantProbeRecord", FoundationFixtures.CLINIC_A_PROBE_ID, requestId, Map.of("safe", true));
        throw new IllegalStateException("rollback fixture");
    }
}
