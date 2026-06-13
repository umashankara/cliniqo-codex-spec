package com.cliniqo.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.cliniqo.audit.repository.AuditEventRepository;
import com.cliniqo.audit.service.AuditService;
import com.cliniqo.audit.support.AuditRollbackFixture;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.enums.UserRole;
import com.cliniqo.support.FoundationFixtures;
import com.cliniqo.support.PostgresIntegrationTest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class AuditTransactionIT extends PostgresIntegrationTest {
    @Autowired
    AuditService auditService;

    @Autowired
    AuditEventRepository auditEventRepository;

    @Autowired
    AuditRollbackFixture rollbackFixture;

    @Test
    @Transactional
    void committedParentActionCommitsAudit() {
        auditService.record(AuditEventType.TEST_MUTATION, FoundationFixtures.CLINIC_A_ADMIN_ID,
                UserRole.CLINIC_ADMIN, FoundationFixtures.CLINIC_A_ID, "TenantProbeRecord",
                FoundationFixtures.CLINIC_A_PROBE_ID, "audit-commit", Map.of("safe", true));
        assertThat(auditEventRepository.findByRequestId("audit-commit")).hasSize(1);
    }

    @Test
    void rolledBackParentActionDoesNotLeaveAudit() {
        assertThatThrownBy(() -> rollbackFixture.writeThenRollback("audit-rollback"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(auditEventRepository.findByRequestId("audit-rollback")).isEmpty();
    }
}
