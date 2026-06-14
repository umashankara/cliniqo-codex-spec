package com.cliniqo.clinic.entity;

import com.cliniqo.common.entity.AuditableRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "reminder_defaults")
public class ReminderDefault extends AuditableRecord {
    @Column(name = "clinic_id", nullable = false, columnDefinition = "uuid")
    private UUID clinicId;
    @Column(nullable = false, length = 32)
    private String type;
    @Column(nullable = false)
    private boolean enabled;
    @Column(name = "send_time_local")
    private LocalTime sendTimeLocal;
    @Column(name = "offset_minutes")
    private Integer offsetMinutes;
    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public LocalTime getSendTimeLocal() { return sendTimeLocal; }
    public void setSendTimeLocal(LocalTime sendTimeLocal) { this.sendTimeLocal = sendTimeLocal; }
    public Integer getOffsetMinutes() { return offsetMinutes; }
    public void setOffsetMinutes(Integer offsetMinutes) { this.offsetMinutes = offsetMinutes; }
}
