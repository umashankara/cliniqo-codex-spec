package com.cliniqo.auth.entity;

import com.cliniqo.common.entity.AuditableRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "default_permission_assignments")
public class DefaultPermissionAssignment extends AuditableRecord {
    @Column(name = "clinic_id", nullable = false, columnDefinition = "uuid")
    private UUID clinicId;
    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;
    @Column(nullable = false, length = 80)
    private String module;
    @Column(nullable = false, length = 32)
    private String level;
    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
