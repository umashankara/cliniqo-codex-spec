package com.cliniqo.auth.service;

import com.cliniqo.auth.entity.RefreshSession;
import com.cliniqo.auth.repository.RefreshSessionRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshSessionRevocationService {
    private final RefreshSessionRepository refreshSessionRepository;

    public RefreshSessionRevocationService(RefreshSessionRepository refreshSessionRepository) {
        this.refreshSessionRepository = refreshSessionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeFamily(UUID familyId, String reason) {
        Instant now = Instant.now();
        for (RefreshSession session : refreshSessionRepository.findByFamilyIdAndRevokedAtIsNull(familyId)) {
            session.setRevokedAt(now);
            session.setRevokedReason(reason);
            refreshSessionRepository.save(session);
        }
    }
}
