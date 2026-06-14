package com.cliniqo.clinic.service;

import com.cliniqo.common.exception.ValidationException;
import com.cliniqo.superadmin.dto.ClinicOnboardingRequest;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import org.springframework.stereotype.Service;

@Service
public class ClinicOnboardingValidationService {
    public void validate(ClinicOnboardingRequest request) {
        try {
            ZoneId.of(request.clinic().timezone());
        } catch (ZoneRulesException ex) {
            throw new ValidationException("timezone must be a valid IANA timezone");
        }
        if (request.clinic().defaultLanguage() == null || request.clinic().defaultLanguage().isBlank()) {
            throw new ValidationException("defaultLanguage is required");
        }
        if (request.operatingHours() == null || request.operatingHours().isEmpty()) {
            throw new ValidationException("operatingHours is required");
        }
        boolean hasOpenDay = false;
        for (ClinicOnboardingRequest.OperatingHourInput hour : request.operatingHours()) {
            if (!hour.closed()) {
                hasOpenDay = true;
                LocalTime open = LocalTime.parse(hour.openTime());
                LocalTime close = LocalTime.parse(hour.closeTime());
                if (!open.isBefore(close)) {
                    throw new ValidationException("openTime must be before closeTime");
                }
            }
        }
        if (!hasOpenDay) {
            throw new ValidationException("At least one operating day must be open");
        }
    }
}
