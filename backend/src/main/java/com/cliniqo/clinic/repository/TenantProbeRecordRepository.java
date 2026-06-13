package com.cliniqo.clinic.repository;

import com.cliniqo.clinic.entity.TenantProbeRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantProbeRecordRepository extends JpaRepository<TenantProbeRecord, UUID> {
    Optional<TenantProbeRecord> findByIdAndClinicIdAndDeletedAtIsNull(UUID id, UUID clinicId);
    List<TenantProbeRecord> findAllByClinicIdAndDeletedAtIsNull(UUID clinicId);
    boolean existsByIdAndClinicIdAndDeletedAtIsNull(UUID id, UUID clinicId);
}
