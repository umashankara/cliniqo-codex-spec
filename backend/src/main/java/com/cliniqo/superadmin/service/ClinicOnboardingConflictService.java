package com.cliniqo.superadmin.service;

import com.cliniqo.auth.repository.UserRepository;
import com.cliniqo.clinic.repository.ClinicRepository;
import com.cliniqo.common.exception.ConflictException;
import com.cliniqo.common.validation.SlugValidator;
import com.cliniqo.publicwebsite.repository.PublicWebsiteSlugReservationRepository;
import com.cliniqo.superadmin.dto.ClinicOnboardingRequest;
import com.cliniqo.whatsapp.repository.WhatsAppMetadataRepository;
import org.springframework.stereotype.Service;

@Service
public class ClinicOnboardingConflictService {
    private final ClinicRepository clinicRepository;
    private final PublicWebsiteSlugReservationRepository publicSlugRepository;
    private final UserRepository userRepository;
    private final WhatsAppMetadataRepository whatsAppRepository;

    public ClinicOnboardingConflictService(
            ClinicRepository clinicRepository,
            PublicWebsiteSlugReservationRepository publicSlugRepository,
            UserRepository userRepository,
            WhatsAppMetadataRepository whatsAppRepository) {
        this.clinicRepository = clinicRepository;
        this.publicSlugRepository = publicSlugRepository;
        this.userRepository = userRepository;
        this.whatsAppRepository = whatsAppRepository;
    }

    public void validate(ClinicOnboardingRequest request) {
        SlugValidator.requireCanonical("clinic.slug", request.clinic().slug());
        SlugValidator.requireCanonical("publicWebsite.slug", request.publicWebsite().slug());
        if (clinicRepository.existsBySlug(request.clinic().slug())) {
            throw new ConflictException("clinic.slug already exists");
        }
        if (publicSlugRepository.existsBySlug(request.publicWebsite().slug())) {
            throw new ConflictException("publicWebsite.slug already exists");
        }
        if (userRepository.existsByEmailIgnoreCase(request.firstAdmin().email())) {
            throw new ConflictException("firstAdmin.email already exists");
        }
        if (whatsAppRepository.existsByDisplayPhoneNumber(request.whatsapp().displayPhoneNumber())) {
            throw new ConflictException("whatsapp.displayPhoneNumber already exists");
        }
        if (whatsAppRepository.existsByPhoneNumberId(request.whatsapp().phoneNumberId())) {
            throw new ConflictException("whatsapp.phoneNumberId already exists");
        }
    }
}
