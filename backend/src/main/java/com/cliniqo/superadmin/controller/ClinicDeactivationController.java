package com.cliniqo.superadmin.controller;

import com.cliniqo.common.dto.ApiResponse;
import com.cliniqo.config.RequestIdFilter;
import com.cliniqo.superadmin.dto.DeactivateClinicRequest;
import com.cliniqo.superadmin.dto.DeactivateClinicResponse;
import com.cliniqo.superadmin.service.ClinicDeactivationService;
import com.cliniqo.superadmin.service.SuperAdminAuthorizationService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/super-admin/clinics")
public class ClinicDeactivationController {
    private final SuperAdminAuthorizationService authorizationService;
    private final ClinicDeactivationService deactivationService;

    public ClinicDeactivationController(SuperAdminAuthorizationService authorizationService, ClinicDeactivationService deactivationService) {
        this.authorizationService = authorizationService;
        this.deactivationService = deactivationService;
    }

    @PostMapping("/{clinicId}/deactivate")
    public ApiResponse<DeactivateClinicResponse> deactivate(@PathVariable UUID clinicId, @Valid @RequestBody DeactivateClinicRequest request) {
        DeactivateClinicResponse response = deactivationService.deactivate(authorizationService.requireSuperAdmin(), clinicId, request.reason());
        return ApiResponse.success(response, RequestIdFilter.currentRequestId());
    }
}
