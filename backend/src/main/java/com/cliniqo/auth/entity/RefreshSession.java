package com.cliniqo.auth.entity;

import com.cliniqo.common.entity.AuditableRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_sessions")
public class RefreshSession extends AuditableRecord {
    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 128)
    private String tokenHash;

    @Column(name = "family_id", nullable = false, columnDefinition = "uuid")
    private UUID familyId;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revoked_reason", length = 80)
    private String revokedReason;

    @Column(name = "replaced_by_session_id", columnDefinition = "uuid")
    private UUID replacedBySessionId;

    @Column(name = "request_id", length = 120)
    private String requestId;

    @Column(name = "created_ip_hash", length = 128)
    private String createdIpHash;

    @Column(name = "user_agent_hash", length = 128)
    private String userAgentHash;

    public boolean isActive(Instant now) {
        return revokedAt == null && expiresAt.isAfter(now);
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public UUID getFamilyId() { return familyId; }
    public void setFamilyId(UUID familyId) { this.familyId = familyId; }
    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public Instant getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(Instant lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
    public String getRevokedReason() { return revokedReason; }
    public void setRevokedReason(String revokedReason) { this.revokedReason = revokedReason; }
    public UUID getReplacedBySessionId() { return replacedBySessionId; }
    public void setReplacedBySessionId(UUID replacedBySessionId) { this.replacedBySessionId = replacedBySessionId; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getCreatedIpHash() { return createdIpHash; }
    public void setCreatedIpHash(String createdIpHash) { this.createdIpHash = createdIpHash; }
    public String getUserAgentHash() { return userAgentHash; }
    public void setUserAgentHash(String userAgentHash) { this.userAgentHash = userAgentHash; }
}
