package com.cliniqo.superadmin.context;

import java.util.UUID;

public class SuperAdminRouteContext {
    private final UUID actorUserId;
    private final UUID targetClinicId;
    private final String requestId;
    private final String purpose;

    public SuperAdminRouteContext(UUID actorUserId, UUID targetClinicId, String requestId, String purpose) {
        this.actorUserId = actorUserId;
        this.targetClinicId = targetClinicId;
        this.requestId = requestId;
        this.purpose = purpose;
    }

    public UUID getActorUserId() { return actorUserId; }
    public UUID getTargetClinicId() { return targetClinicId; }
    public String getRequestId() { return requestId; }
    public String getPurpose() { return purpose; }
}
