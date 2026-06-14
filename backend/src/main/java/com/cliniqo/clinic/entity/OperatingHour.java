package com.cliniqo.clinic.entity;

import com.cliniqo.common.entity.AuditableRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "clinic_operating_hours")
public class OperatingHour extends AuditableRecord {
    @Column(name = "clinic_id", nullable = false, columnDefinition = "uuid")
    private UUID clinicId;
    @Column(name = "day_of_week", nullable = false, length = 16)
    private String dayOfWeek;
    @Column(name = "open_time")
    private LocalTime openTime;
    @Column(name = "close_time")
    private LocalTime closeTime;
    @Column(nullable = false)
    private boolean closed;
    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getOpenTime() { return openTime; }
    public void setOpenTime(LocalTime openTime) { this.openTime = openTime; }
    public LocalTime getCloseTime() { return closeTime; }
    public void setCloseTime(LocalTime closeTime) { this.closeTime = closeTime; }
    public boolean isClosed() { return closed; }
    public void setClosed(boolean closed) { this.closed = closed; }
}
