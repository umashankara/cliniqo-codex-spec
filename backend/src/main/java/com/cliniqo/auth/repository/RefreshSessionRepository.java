package com.cliniqo.auth.repository;

import com.cliniqo.auth.entity.RefreshSession;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {
    Optional<RefreshSession> findByTokenHash(String tokenHash);
    List<RefreshSession> findByFamilyIdAndRevokedAtIsNull(UUID familyId);
    List<RefreshSession> findByUserIdAndExpiresAtAfterAndRevokedAtIsNull(UUID userId, Instant now);
    List<RefreshSession> findByUserIdInAndRevokedAtIsNull(List<UUID> userIds);

    @Modifying
    @Query("update RefreshSession r set r.revokedAt = :now, r.revokedReason = :reason where r.userId in :userIds and r.revokedAt is null")
    int revokeActiveByUserIds(List<UUID> userIds, Instant now, String reason);
}
