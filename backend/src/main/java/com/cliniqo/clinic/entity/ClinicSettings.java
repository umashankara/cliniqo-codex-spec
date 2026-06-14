package com.cliniqo.clinic.entity;

import com.cliniqo.common.entity.AuditableRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "clinic_settings")
public class ClinicSettings extends AuditableRecord {
    @Column(name = "clinic_id", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID clinicId;
    @Column(name = "slot_duration_minutes", nullable = false)
    private int slotDurationMinutes;
    @Column(name = "max_advance_booking_days", nullable = false)
    private int maxAdvanceBookingDays;
    @Column(name = "min_booking_notice_minutes", nullable = false)
    private int minBookingNoticeMinutes;
    @Column(name = "cancellation_cutoff_minutes", nullable = false)
    private int cancellationCutoffMinutes;
    @Column(name = "reschedule_cutoff_minutes", nullable = false)
    private int rescheduleCutoffMinutes;
    @Column(name = "default_language", nullable = false, length = 16)
    private String defaultLanguage;
    @Column(name = "public_website_enabled", nullable = false)
    private boolean publicWebsiteEnabled;
    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(int slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }
    public int getMaxAdvanceBookingDays() { return maxAdvanceBookingDays; }
    public void setMaxAdvanceBookingDays(int maxAdvanceBookingDays) { this.maxAdvanceBookingDays = maxAdvanceBookingDays; }
    public int getMinBookingNoticeMinutes() { return minBookingNoticeMinutes; }
    public void setMinBookingNoticeMinutes(int minBookingNoticeMinutes) { this.minBookingNoticeMinutes = minBookingNoticeMinutes; }
    public int getCancellationCutoffMinutes() { return cancellationCutoffMinutes; }
    public void setCancellationCutoffMinutes(int cancellationCutoffMinutes) { this.cancellationCutoffMinutes = cancellationCutoffMinutes; }
    public int getRescheduleCutoffMinutes() { return rescheduleCutoffMinutes; }
    public void setRescheduleCutoffMinutes(int rescheduleCutoffMinutes) { this.rescheduleCutoffMinutes = rescheduleCutoffMinutes; }
    public String getDefaultLanguage() { return defaultLanguage; }
    public void setDefaultLanguage(String defaultLanguage) { this.defaultLanguage = defaultLanguage; }
    public boolean isPublicWebsiteEnabled() { return publicWebsiteEnabled; }
    public void setPublicWebsiteEnabled(boolean publicWebsiteEnabled) { this.publicWebsiteEnabled = publicWebsiteEnabled; }
}
