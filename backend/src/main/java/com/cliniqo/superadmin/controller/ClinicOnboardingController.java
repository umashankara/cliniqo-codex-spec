package com.cliniqo.superadmin.controller;

import com.cliniqo.common.dto.ApiResponse;
import com.cliniqo.config.RequestIdFilter;
import com.cliniqo.superadmin.dto.ClinicOnboardingRequest;
import com.cliniqo.superadmin.dto.ClinicOnboardingResponse;
import com.cliniqo.superadmin.service.ClinicOnboardingService;
import com.cliniqo.superadmin.service.SuperAdminAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/super-admin/onboarding")
public class ClinicOnboardingController {
    private final SuperAdminAuthorizationService authorizationService;
    private final ClinicOnboardingService onboardingService;

    public ClinicOnboardingController(SuperAdminAuthorizationService authorizationService, ClinicOnboardingService onboardingService) {
        this.authorizationService = authorizationService;
        this.onboardingService = onboardingService;
    }

    @PostMapping("/clinics")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ClinicOnboardingResponse> onboard(
            @Valid @RequestBody ClinicOnboardingRequest request,
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey) {
        ClinicOnboardingResponse response = onboardingService.onboard(authorizationService.requireSuperAdmin(), request, idempotencyKey);
        return ApiResponse.success(response, RequestIdFilter.currentRequestId());
    }
}
