package com.cliniqo.superadmin.mapper;

import com.cliniqo.clinic.entity.Clinic;
import com.cliniqo.common.enums.ClinicStatus;
import com.cliniqo.superadmin.dto.ClinicOnboardingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ClinicOnboardingMapper {
    private final ObjectMapper objectMapper;

    public ClinicOnboardingMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Clinic clinic(ClinicOnboardingRequest request) {
        Clinic clinic = new Clinic();
        clinic.setName(request.clinic().name());
        clinic.setSlug(request.clinic().slug());
        clinic.setAddress(request.clinic().address());
        clinic.setCountry(request.clinic().country());
        clinic.setTimezone(request.clinic().timezone());
        clinic.setDefaultLanguage(request.clinic().defaultLanguage());
        clinic.setPrimaryPhone(request.clinic().primaryPhone());
        clinic.setEmail(request.clinic().email());
        clinic.setStatus(ClinicStatus.ACTIVE);
        if (request.clinic().logoMetadata() != null) {
            try {
                clinic.setLogoMetadata(objectMapper.writeValueAsString(request.clinic().logoMetadata()));
            } catch (Exception ignored) {
                clinic.setLogoMetadata("{}");
            }
        }
        return clinic;
    }
}
