package com.cliniqo.publicwebsite.entity;

import com.cliniqo.common.entity.AuditableRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "public_website_slug_reservations")
public class PublicWebsiteSlugReservation extends AuditableRecord {
    @Column(name = "clinic_id", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID clinicId;
    @Column(nullable = false, unique = true, length = 80)
    private String slug;
    @Column(nullable = false)
    private boolean enabled;
    @Column(name = "reserved_at", nullable = false)
    private Instant reservedAt = Instant.now();
    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Instant getReservedAt() { return reservedAt; }
    public void setReservedAt(Instant reservedAt) { this.reservedAt = reservedAt; }
}
