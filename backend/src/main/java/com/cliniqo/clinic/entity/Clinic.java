package com.cliniqo.clinic.entity;

import com.cliniqo.common.entity.AuditableRecord;
import com.cliniqo.common.enums.ClinicStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public ClinicStatus getStatus() { return status; }
    public void setStatus(ClinicStatus status) { this.status = status; }
}
