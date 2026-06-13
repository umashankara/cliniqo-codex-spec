package com.cliniqo.auth.service;

import com.cliniqo.auth.entity.RefreshSession;
import com.cliniqo.auth.entity.User;
import com.cliniqo.auth.repository.RefreshSessionRepository;
import com.cliniqo.common.exception.UnauthorizedException;
import com.cliniqo.config.RequestIdFilter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshSessionService {
    private final RefreshSessionRepository refreshSessionRepository;
    private final RefreshSessionRevocationService revocationService;
    private final long refreshTokenDays;

    public RefreshSessionService(
            RefreshSessionRepository refreshSessionRepository,
            RefreshSessionRevocationService revocationService,
            @Value("${cliniqo.security.refresh-token-days}") long refreshTokenDays) {
        this.refreshSessionRepository = refreshSessionRepository;
        this.revocationService = revocationService;
        this.refreshTokenDays = refreshTokenDays;
    }

    public IssuedRefreshSession issue(User user) {
        return issue(user, UUID.randomUUID());
    }

    public RotatedRefreshSession rotate(String rawToken) {
        RefreshSession existing = refreshSessionRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh session"));
        Instant now = Instant.now();
        if (!existing.isActive(now)) {
            revocationService.revokeFamily(existing.getFamilyId(), "reuse_detected");
            throw new UnauthorizedException("Invalid refresh session");
        }
        existing.setLastUsedAt(now);
        existing.setRevokedAt(now);
        existing.setRevokedReason("rotated");
        IssuedRefreshSession next = issue(existing.getUserId(), existing.getFamilyId());
        existing.setReplacedBySessionId(next.session().getId());
        refreshSessionRepository.save(existing);
        return new RotatedRefreshSession(existing, next);
    }

    public void revoke(String rawToken, String reason) {
        refreshSessionRepository.findByTokenHash(hash(rawToken)).ifPresent(session -> {
            session.setRevokedAt(Instant.now());
            session.setRevokedReason(reason);
            refreshSessionRepository.save(session);
        });
    }

    private IssuedRefreshSession issue(User user, UUID familyId) {
        return issue(user.getId(), familyId);
    }

    private IssuedRefreshSession issue(UUID userId, UUID familyId) {
        String rawToken = UUID.randomUUID() + "." + UUID.randomUUID();
        RefreshSession session = new RefreshSession();
        session.setUserId(userId);
        session.setTokenHash(hash(rawToken));
        session.setFamilyId(familyId);
        session.setIssuedAt(Instant.now());
        session.setExpiresAt(Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS));
        session.setRequestId(RequestIdFilter.currentRequestId());
        RefreshSession saved = refreshSessionRepository.save(session);
        return new IssuedRefreshSession(rawToken, saved);
    }

    public static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }

    public record IssuedRefreshSession(String rawToken, RefreshSession session) {}
    public record RotatedRefreshSession(RefreshSession previous, IssuedRefreshSession next) {}
}
