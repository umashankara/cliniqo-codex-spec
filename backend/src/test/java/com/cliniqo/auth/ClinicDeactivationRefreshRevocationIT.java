package com.cliniqo.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cliniqo.auth.repository.RefreshSessionRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ClinicDeactivationRefreshRevocationIT {
    @Test
    void repositoryHasBulkRevocationMethod() throws Exception {
        assertTrue(RefreshSessionRepository.class.getMethod("revokeActiveByUserIds", List.class, Instant.class, String.class).getReturnType().equals(int.class));
    }
}
