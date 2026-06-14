package com.cliniqo.superadmin.controller;

import com.cliniqo.common.dto.ApiResponse;
import com.cliniqo.config.RequestIdFilter;
import com.cliniqo.superadmin.dto.BootstrapStatusResponse;
import com.cliniqo.superadmin.service.SuperAdminAuthorizationService;
import com.cliniqo.superadmin.service.SuperAdminBootstrapAuditService;
import com.cliniqo.superadmin.service.SuperAdminBootstrapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/super-admin/bootstrap")
public class SuperAdminBootstrapController {
    private final SuperAdminAuthorizationService authorizationService;
    private final SuperAdminBootstrapService bootstrapService;
    private final SuperAdminBootstrapAuditService auditService;

    public SuperAdminBootstrapController(SuperAdminAuthorizationService authorizationService, SuperAdminBootstrapService bootstrapService, SuperAdminBootstrapAuditService auditService) {
        this.authorizationService = authorizationService;
        this.bootstrapService = bootstrapService;
        this.auditService = auditService;
    }

    @GetMapping("/status")
    public ApiResponse<BootstrapStatusResponse> status() {
        var actor = authorizationService.requireSuperAdmin();
        BootstrapStatusResponse status = bootstrapService.status();
        auditService.available(actor, status.activeSuperAdminCount());
        return ApiResponse.success(status, RequestIdFilter.currentRequestId());
    }
}
