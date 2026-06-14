package com.cliniqo.superadmin.service;

import com.cliniqo.common.exception.IdempotencyConflictException;
import com.cliniqo.superadmin.entity.OnboardingRequestRecord;
import com.cliniqo.superadmin.repository.OnboardingRequestRecordRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminIdempotencyService {
    private final OnboardingRequestRecordRepository repository;

    public SuperAdminIdempotencyService(OnboardingRequestRecordRepository repository) {
        this.repository = repository;
    }

    public Optional<OnboardingRequestRecord> find(String key, String fingerprint) {
        return repository.findByIdempotencyKey(key).map(record -> {
            if (!record.getRequestFingerprint().equals(fingerprint)) {
                throw new IdempotencyConflictException("Idempotency key was used with a different request");
            }
            return record;
        });
    }

    public OnboardingRequestRecord start(String key, String fingerprint, UUID actorUserId) {
        OnboardingRequestRecord record = new OnboardingRequestRecord();
        record.setIdempotencyKey(key);
        record.setRequestFingerprint(fingerprint);
        record.setActorUserId(actorUserId);
        record.setStatus("IN_PROGRESS");
        return repository.save(record);
    }

    public void succeed(OnboardingRequestRecord record, UUID clinicId) {
        record.setStatus("SUCCEEDED");
        record.setClinicId(clinicId);
        record.setCompletedAt(Instant.now());
        repository.save(record);
    }
}
