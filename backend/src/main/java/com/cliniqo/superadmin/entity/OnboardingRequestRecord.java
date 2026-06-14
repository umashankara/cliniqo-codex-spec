package com.cliniqo.superadmin.entity;

import com.cliniqo.common.entity.AuditableRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "onboarding_request_records")
public class OnboardingRequestRecord extends AuditableRecord {
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 128)
    private String idempotencyKey;
    @Column(name = "request_fingerprint", nullable = false, length = 128)
    private String requestFingerprint;
    @Column(name = "actor_user_id", nullable = false, columnDefinition = "uuid")
    private UUID actorUserId;
    @Column(nullable = false, length = 32)
    private String status;
    @Column(name = "clinic_id", columnDefinition = "uuid")
    private UUID clinicId;
    @Column(name = "safe_result_summary", columnDefinition = "text")
    private String safeResultSummary = "{}";
    @Column(name = "completed_at")
    private Instant completedAt;
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public String getRequestFingerprint() { return requestFingerprint; }
    public void setRequestFingerprint(String requestFingerprint) { this.requestFingerprint = requestFingerprint; }
    public UUID getActorUserId() { return actorUserId; }
    public void setActorUserId(UUID actorUserId) { this.actorUserId = actorUserId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public String getSafeResultSummary() { return safeResultSummary; }
    public void setSafeResultSummary(String safeResultSummary) { this.safeResultSummary = safeResultSummary; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
