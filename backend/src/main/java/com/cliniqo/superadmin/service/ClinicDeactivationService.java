package com.cliniqo.superadmin.service;

import com.cliniqo.auth.service.ClinicRefreshSessionRevocationService;
import com.cliniqo.clinic.entity.Clinic;
import com.cliniqo.clinic.repository.ClinicRepository;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.enums.ClinicStatus;
import com.cliniqo.common.exception.ResourceNotFoundException;
import com.cliniqo.common.security.UserPrincipal;
import com.cliniqo.superadmin.dto.DeactivateClinicResponse;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClinicDeactivationService {
    private final ClinicRepository clinicRepository;
    private final ClinicRefreshSessionRevocationService revocationService;
    private final ClinicOnboardingAuditService auditService;

    public ClinicDeactivationService(ClinicRepository clinicRepository, ClinicRefreshSessionRevocationService revocationService, ClinicOnboardingAuditService auditService) {
        this.clinicRepository = clinicRepository;
        this.revocationService = revocationService;
        this.auditService = auditService;
    }

    @Transactional
    public DeactivateClinicResponse deactivate(UserPrincipal actor, UUID clinicId, String reason) {
        Clinic clinic = clinicRepository.findById(clinicId).orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));
        boolean alreadyInactive = clinic.getStatus() == ClinicStatus.INACTIVE;
        int revoked = revocationService.revokeForClinic(clinicId, "clinic_deactivated");
        if (!alreadyInactive) {
            clinic.setStatus(ClinicStatus.INACTIVE);
            clinic.setDeactivatedAt(Instant.now());
            clinic.setDeactivatedBy(actor.getUserId());
            clinic.setDeactivationReason(reason);
            clinicRepository.save(clinic);
        }
        auditService.record(actor, AuditEventType.CLINIC_DEACTIVATED, clinicId, "Clinic", clinicId, Map.of("reason", reason));
        auditService.record(actor, AuditEventType.CLINIC_REFRESH_SESSIONS_REVOKED, clinicId, "Clinic", clinicId, Map.of("revokedRefreshSessionCount", revoked));
        return new DeactivateClinicResponse(clinicId, "INACTIVE", revoked, alreadyInactive);
    }
}
