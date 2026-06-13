package com.cliniqo.superadmin.controller;

import java.util.UUID;

public class SuperAdminContextResponse {
    private UUID targetClinicId;
    private boolean contextAccepted;

    public SuperAdminContextResponse(UUID targetClinicId, boolean contextAccepted) {
        this.targetClinicId = targetClinicId;
        this.contextAccepted = contextAccepted;
    }

    public UUID getTargetClinicId() { return targetClinicId; }
    public boolean isContextAccepted() { return contextAccepted; }
}
