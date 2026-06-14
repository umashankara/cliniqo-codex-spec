package com.cliniqo.superadmin.repository;

import com.cliniqo.superadmin.entity.OnboardingRequestRecord;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingRequestRecordRepository extends JpaRepository<OnboardingRequestRecord, UUID> {
    Optional<OnboardingRequestRecord> findByIdempotencyKey(String idempotencyKey);
}
