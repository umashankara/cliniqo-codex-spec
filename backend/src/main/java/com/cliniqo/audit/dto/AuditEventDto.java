package com.cliniqo.audit.dto;

import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.enums.UserRole;
import java.time.Instant;
import java.util.UUID;

public class AuditEventDto {
    private UUID id;
    private AuditEventType eventType;
    private UUID actorUserId;
    private UserRole actorRole;
    private UUID clinicId;
    private String targetType;
    private UUID targetId;
    private String requestId;
    private String metadata;
    private Instant occurredAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public AuditEventType getEventType() { return eventType; }
    public void setEventType(AuditEventType eventType) { this.eventType = eventType; }
    public UUID getActorUserId() { return actorUserId; }
    public void setActorUserId(UUID actorUserId) { this.actorUserId = actorUserId; }
    public UserRole getActorRole() { return actorRole; }
    public void setActorRole(UserRole actorRole) { this.actorRole = actorRole; }
    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
}
