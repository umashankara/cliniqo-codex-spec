package com.cliniqo.auth.service;

import com.cliniqo.audit.service.AuditService;
import com.cliniqo.auth.entity.User;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.enums.UserRole;
import com.cliniqo.config.RequestIdFilter;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AuthAuditPublisher {
    private final AuditService auditService;

    public AuthAuditPublisher(AuditService auditService) {
        this.auditService = auditService;
    }

    public void loginSuccess(User user) {
        record(AuditEventType.AUTH_LOGIN_SUCCESS, user.getId(), user.getRole(), user.getClinicId(), "User", user.getId(), Map.of("email", user.getEmail()));
    }

    public void refreshRotated(User user) {
        record(AuditEventType.REFRESH_ROTATED, user.getId(), user.getRole(), user.getClinicId(), "RefreshSession", user.getId(), Map.of());
    }

    public void logout(User user) {
        record(AuditEventType.AUTH_LOGOUT, user.getId(), user.getRole(), user.getClinicId(), "User", user.getId(), Map.of());
    }

    public void refreshReuse(UUID userId, UserRole role, UUID clinicId) {
        record(AuditEventType.REFRESH_REUSE_DETECTED, userId, role, clinicId, "RefreshSession", userId, Map.of());
    }

    private void record(AuditEventType eventType, UUID actorUserId, UserRole role, UUID clinicId, String targetType, UUID targetId, Map<String, Object> metadata) {
        auditService.record(eventType, actorUserId, role, clinicId, targetType, targetId, RequestIdFilter.currentRequestId(), metadata);
    }
}
