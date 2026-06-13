package com.cliniqo.auth.repository;

import com.cliniqo.auth.entity.RefreshSession;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {
    Optional<RefreshSession> findByTokenHash(String tokenHash);
    List<RefreshSession> findByFamilyIdAndRevokedAtIsNull(UUID familyId);
    List<RefreshSession> findByUserIdAndExpiresAtAfterAndRevokedAtIsNull(UUID userId, Instant now);
}
