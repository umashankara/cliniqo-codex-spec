package com.cliniqo.audit.entity;

import com.cliniqo.common.entity.AuditableRecord;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "audit_events")
public class AuditEvent extends AuditableRecord {
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 80)
    private AuditEventType eventType;

    @Column(name = "actor_user_id", columnDefinition = "uuid")
    private UUID actorUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_role", length = 32)
    private UserRole actorRole;

    @Column(name = "clinic_id", columnDefinition = "uuid")
    private UUID clinicId;

    @Column(name = "target_type", length = 80)
    private String targetType;

    @Column(name = "target_id", columnDefinition = "uuid")
    private UUID targetId;

    @Column(name = "request_id", length = 120)
    private String requestId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String metadata = "{}";

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt = Instant.now();

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
