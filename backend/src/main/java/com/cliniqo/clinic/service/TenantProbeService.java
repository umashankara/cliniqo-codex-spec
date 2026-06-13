package com.cliniqo.clinic.service;

import com.cliniqo.audit.service.AuditService;
import com.cliniqo.clinic.context.TenantContext;
import com.cliniqo.clinic.context.TenantTamperDetector;
import com.cliniqo.clinic.dto.TenantProbeResponse;
import com.cliniqo.clinic.entity.TenantProbeRecord;
import com.cliniqo.clinic.repository.TenantProbeRecordRepository;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.exception.ResourceNotFoundException;
import com.cliniqo.config.RequestIdFilter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantProbeService {
    private final TenantProbeRecordRepository repository;
    private final TenantTamperDetector tamperDetector;
    private final AuditService auditService;

    public TenantProbeService(TenantProbeRecordRepository repository, TenantTamperDetector tamperDetector, AuditService auditService) {
        this.repository = repository;
        this.tamperDetector = tamperDetector;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public TenantProbeResponse get(UUID recordId) {
        UUID clinicId = TenantContext.requiredClinicId();
        TenantProbeRecord record = repository.findByIdAndClinicIdAndDeletedAtIsNull(recordId, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return new TenantProbeResponse(record.getId(), true);
    }

    @Transactional(readOnly = true)
    public List<TenantProbeResponse> list() {
        UUID clinicId = TenantContext.requiredClinicId();
        return repository.findAllByClinicIdAndDeletedAtIsNull(clinicId).stream()
                .map(record -> new TenantProbeResponse(record.getId(), true))
                .toList();
    }

    @Transactional
    public TenantProbeResponse update(UUID recordId, Map<String, Object> payload) {
        TenantContext context = TenantContext.get();
        if (tamperDetector.containsTenantHint(payload)) {
            auditService.record(AuditEventType.TENANT_TAMPER, context.getUserId(), context.getRole(), context.getClinicId(),
                    "TenantProbeRecord", recordId, RequestIdFilter.currentRequestId(), Map.of("clinicId", "[client supplied]"));
        }
        return get(recordId);
    }

    @Transactional
    public void delete(UUID recordId) {
        UUID clinicId = TenantContext.requiredClinicId();
        TenantProbeRecord record = repository.findByIdAndClinicIdAndDeletedAtIsNull(recordId, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        TenantContext context = TenantContext.get();
        record.softDelete(context.getUserId());
        repository.save(record);
    }

    @Transactional(readOnly = true)
    public boolean existsForCurrentClinic(UUID recordId) {
        UUID clinicId = TenantContext.requiredClinicId();
        return repository.existsByIdAndClinicIdAndDeletedAtIsNull(recordId, clinicId);
    }
}
