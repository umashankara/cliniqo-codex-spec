package com.cliniqo.audit;

import static org.assertj.core.api.Assertions.assertThat;

import com.cliniqo.common.enums.AuditEventType;
import org.junit.jupiter.api.Test;

class AuditEventTypeIT {
    @Test
    void foundationAuditCategoriesExist() {
        assertThat(AuditEventType.values()).contains(
                AuditEventType.AUTH_LOGIN_SUCCESS,
                AuditEventType.REFRESH_ROTATED,
                AuditEventType.TENANT_TAMPER,
                AuditEventType.SOFT_DELETE,
                AuditEventType.SUPER_ADMIN_TARGET_CONTEXT);
    }
}
