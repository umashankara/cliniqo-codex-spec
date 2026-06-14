package com.cliniqo.clinic.entity;

import com.cliniqo.common.entity.AuditableRecord;
import com.cliniqo.common.enums.ClinicStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clinics")
public class Clinic extends AuditableRecord {
    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ClinicStatus status = ClinicStatus.ACTIVE;

    @Column(length = 500)
    private String address;

    @Column(length = 80)
    private String country;

    @Column(nullable = false, length = 80)
    private String timezone = "UTC";

    @Column(name = "default_language", nullable = false, length = 16)
    private String defaultLanguage = "en";

    @Column(name = "primary_phone", length = 32)
    private String primaryPhone;

    @Column(length = 255)
    private String email;

    @Column(name = "logo_metadata", columnDefinition = "text")
    private String logoMetadata;

    @Column(name = "deactivated_at")
    private Instant deactivatedAt;

    @Column(name = "deactivated_by", columnDefinition = "uuid")
    private UUID deactivatedBy;

    @Column(name = "deactivation_reason", length = 500)
    private String deactivationReason;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public ClinicStatus getStatus() { return status; }
    public void setStatus(ClinicStatus status) { this.status = status; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public String getDefaultLanguage() { return defaultLanguage; }
    public void setDefaultLanguage(String defaultLanguage) { this.defaultLanguage = defaultLanguage; }
    public String getPrimaryPhone() { return primaryPhone; }
    public void setPrimaryPhone(String primaryPhone) { this.primaryPhone = primaryPhone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLogoMetadata() { return logoMetadata; }
    public void setLogoMetadata(String logoMetadata) { this.logoMetadata = logoMetadata; }
    public Instant getDeactivatedAt() { return deactivatedAt; }
    public void setDeactivatedAt(Instant deactivatedAt) { this.deactivatedAt = deactivatedAt; }
    public UUID getDeactivatedBy() { return deactivatedBy; }
    public void setDeactivatedBy(UUID deactivatedBy) { this.deactivatedBy = deactivatedBy; }
    public String getDeactivationReason() { return deactivationReason; }
    public void setDeactivationReason(String deactivationReason) { this.deactivationReason = deactivationReason; }
}
