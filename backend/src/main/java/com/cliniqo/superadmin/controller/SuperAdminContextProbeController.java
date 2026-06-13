package com.cliniqo.superadmin.controller;

import com.cliniqo.audit.service.AuditService;
import com.cliniqo.common.dto.ApiResponse;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.enums.UserRole;
import com.cliniqo.common.exception.ForbiddenException;
import com.cliniqo.common.security.CurrentUserService;
import com.cliniqo.common.security.UserPrincipal;
import com.cliniqo.config.RequestIdFilter;
import java.util.Map;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/super-admin/clinics")
public class SuperAdminContextProbeController {
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    public SuperAdminContextProbeController(CurrentUserService currentUserService, AuditService auditService) {
        this.currentUserService = currentUserService;
        this.auditService = auditService;
    }

    @GetMapping("/{clinicId}/context-probe")
    @Transactional
    ApiResponse<SuperAdminContextResponse> contextProbe(@PathVariable UUID clinicId) {
        UserPrincipal principal = currentUserService.requirePrincipal();
        if (principal.getRole() != UserRole.SUPER_ADMIN) {
            throw new ForbiddenException("SUPER_ADMIN role is required");
        }
        auditService.record(AuditEventType.SUPER_ADMIN_TARGET_CONTEXT, principal.getUserId(), principal.getRole(),
                clinicId, "Clinic", clinicId, RequestIdFilter.currentRequestId(), Map.of("purpose", "context-probe"));
        return ApiResponse.success(new SuperAdminContextResponse(clinicId, true), RequestIdFilter.currentRequestId());
    }
}
