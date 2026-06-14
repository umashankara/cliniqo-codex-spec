package com.cliniqo.auth.service;

import com.cliniqo.auth.entity.User;
import com.cliniqo.auth.repository.RefreshSessionRepository;
import com.cliniqo.auth.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClinicRefreshSessionRevocationService {
    private final UserRepository userRepository;
    private final RefreshSessionRepository refreshSessionRepository;

    public ClinicRefreshSessionRevocationService(UserRepository userRepository, RefreshSessionRepository refreshSessionRepository) {
        this.userRepository = userRepository;
        this.refreshSessionRepository = refreshSessionRepository;
    }

    @Transactional
    public int revokeForClinic(UUID clinicId, String reason) {
        List<UUID> userIds = userRepository.findByClinicId(clinicId).stream().map(User::getId).toList();
        if (userIds.isEmpty()) {
            return 0;
        }
        return refreshSessionRepository.revokeActiveByUserIds(userIds, Instant.now(), reason);
    }
}
