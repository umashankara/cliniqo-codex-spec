package com.cliniqo.auth.entity;

import com.cliniqo.common.entity.AuditableRecord;
import com.cliniqo.common.enums.UserRole;
import com.cliniqo.common.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends AuditableRecord {
    @Column(name = "clinic_id", columnDefinition = "uuid")
    private UUID clinicId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "force_password_reset", nullable = false)
    private boolean forcePasswordReset;

    @Column(name = "full_name", length = 160)
    private String fullName;

    @Column(length = 32)
    private String phone;

    @Column(name = "temporary_credential_issued_at")
    private Instant temporaryCredentialIssuedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by", columnDefinition = "uuid")
    private UUID deletedBy;

    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public boolean isForcePasswordReset() { return forcePasswordReset; }
    public void setForcePasswordReset(boolean forcePasswordReset) { this.forcePasswordReset = forcePasswordReset; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Instant getTemporaryCredentialIssuedAt() { return temporaryCredentialIssuedAt; }
    public void setTemporaryCredentialIssuedAt(Instant temporaryCredentialIssuedAt) { this.temporaryCredentialIssuedAt = temporaryCredentialIssuedAt; }
    public Instant getDeletedAt() { return deletedAt; }
    public UUID getDeletedBy() { return deletedBy; }
}
