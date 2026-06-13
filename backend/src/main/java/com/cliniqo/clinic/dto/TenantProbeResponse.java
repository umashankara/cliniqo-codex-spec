package com.cliniqo.clinic.dto;

import java.util.UUID;

public class TenantProbeResponse {
    private UUID id;
    private boolean clinicOwned;

    public TenantProbeResponse(UUID id, boolean clinicOwned) {
        this.id = id;
        this.clinicOwned = clinicOwned;
    }

    public UUID getId() { return id; }
    public boolean isClinicOwned() { return clinicOwned; }
}
