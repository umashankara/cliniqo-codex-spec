package com.cliniqo.clinic.controller;

import com.cliniqo.clinic.dto.TenantProbeResponse;
import com.cliniqo.clinic.service.TenantProbeService;
import com.cliniqo.common.dto.ApiResponse;
import com.cliniqo.config.RequestIdFilter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/foundation/tenant-probe")
public class TenantProbeController {
    private final TenantProbeService tenantProbeService;

    public TenantProbeController(TenantProbeService tenantProbeService) {
        this.tenantProbeService = tenantProbeService;
    }

    @GetMapping("/{recordId}")
    ApiResponse<TenantProbeResponse> get(@PathVariable UUID recordId) {
        return ApiResponse.success(tenantProbeService.get(recordId), RequestIdFilter.currentRequestId());
    }

    @GetMapping
    ApiResponse<List<TenantProbeResponse>> list() {
        return ApiResponse.success(tenantProbeService.list(), RequestIdFilter.currentRequestId());
    }

    @PatchMapping("/{recordId}")
    ApiResponse<TenantProbeResponse> update(@PathVariable UUID recordId, @RequestBody(required = false) Map<String, Object> payload) {
        return ApiResponse.success(tenantProbeService.update(recordId, payload == null ? Map.of() : payload), RequestIdFilter.currentRequestId());
    }

    @DeleteMapping("/{recordId}")
    ApiResponse<Void> delete(@PathVariable UUID recordId) {
        tenantProbeService.delete(recordId);
        return ApiResponse.empty(RequestIdFilter.currentRequestId());
    }
}
