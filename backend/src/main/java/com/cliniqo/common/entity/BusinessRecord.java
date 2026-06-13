package com.cliniqo.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLRestriction;

@MappedSuperclass
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "clinicId", type = UUID.class))
@Filter(name = "tenantFilter", condition = "clinic_id = :clinicId")
@SQLRestriction("deleted_at IS NULL")
public abstract class BusinessRecord extends AuditableRecord {
    @Column(name = "clinic_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID clinicId;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by", columnDefinition = "uuid")
    private UUID deletedBy;

    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public Instant getDeletedAt() { return deletedAt; }
    public UUID getDeletedBy() { return deletedBy; }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete(UUID actorId) {
        this.deletedAt = Instant.now();
        this.deletedBy = actorId;
    }

    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
