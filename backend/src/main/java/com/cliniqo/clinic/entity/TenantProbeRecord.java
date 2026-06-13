package com.cliniqo.clinic.entity;

import com.cliniqo.common.entity.BusinessRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenant_probe_records")
public class TenantProbeRecord extends BusinessRecord {
    @Column(nullable = false, length = 160)
    private String label;

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
