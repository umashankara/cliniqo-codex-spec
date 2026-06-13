package com.cliniqo.audit.service;

import com.cliniqo.audit.entity.AuditEvent;
import com.cliniqo.audit.repository.AuditEventRepository;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.enums.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultAuditService implements AuditService {
    private final AuditEventRepository auditEventRepository;
    private final AuditMetadataRedactor redactor;
    private final ObjectMapper objectMapper;

    public DefaultAuditService(AuditEventRepository auditEventRepository, AuditMetadataRedactor redactor, ObjectMapper objectMapper) {
        this.auditEventRepository = auditEventRepository;
        this.redactor = redactor;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void record(AuditEventType eventType, UUID actorUserId, UserRole actorRole, UUID clinicId,
            String targetType, UUID targetId, String requestId, Map<String, Object> metadata) {
        AuditEvent event = new AuditEvent();
        event.setEventType(eventType);
        event.setActorUserId(actorUserId);
        event.setActorRole(actorRole);
        event.setClinicId(clinicId);
        event.setTargetType(targetType);
        event.setTargetId(targetId);
        event.setRequestId(requestId);
        event.setOccurredAt(Instant.now());
        event.setMetadata(toJson(redactor.redact(metadata)));
        auditEventRepository.save(event);
    }

    private String toJson(Map<String, Object> metadata) {
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }
}
