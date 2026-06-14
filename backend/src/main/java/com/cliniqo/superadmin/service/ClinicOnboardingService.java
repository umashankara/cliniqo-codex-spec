package com.cliniqo.superadmin.service;

import com.cliniqo.auth.entity.User;
import com.cliniqo.auth.service.ClinicAdminProvisioningService;
import com.cliniqo.auth.service.TemporaryCredentialService;
import com.cliniqo.clinic.entity.Clinic;
import com.cliniqo.clinic.entity.ClinicSettings;
import com.cliniqo.clinic.entity.OperatingHour;
import com.cliniqo.clinic.entity.ReminderDefault;
import com.cliniqo.clinic.repository.ClinicRepository;
import com.cliniqo.clinic.repository.ClinicSettingsRepository;
import com.cliniqo.clinic.repository.FaqSeedRepository;
import com.cliniqo.clinic.repository.OperatingHourRepository;
import com.cliniqo.clinic.repository.ReminderDefaultRepository;
import com.cliniqo.clinic.service.ClinicOnboardingValidationService;
import com.cliniqo.clinic.service.DefaultFaqSeedProvider;
import com.cliniqo.common.crypto.CredentialEncryptor;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.idempotency.IdempotencyKey;
import com.cliniqo.common.idempotency.RequestFingerprint;
import com.cliniqo.common.security.UserPrincipal;
import com.cliniqo.publicwebsite.entity.PublicWebsiteSlugReservation;
import com.cliniqo.publicwebsite.repository.PublicWebsiteSlugReservationRepository;
import com.cliniqo.superadmin.dto.ClinicOnboardingRequest;
import com.cliniqo.superadmin.dto.ClinicOnboardingResponse;
import com.cliniqo.superadmin.entity.OnboardingRequestRecord;
import com.cliniqo.superadmin.mapper.ClinicOnboardingMapper;
import com.cliniqo.whatsapp.entity.WhatsAppMetadata;
import com.cliniqo.whatsapp.repository.WhatsAppMetadataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClinicOnboardingService {
    private final ClinicRepository clinicRepository;
    private final ClinicSettingsRepository settingsRepository;
    private final OperatingHourRepository operatingHourRepository;
    private final ReminderDefaultRepository reminderDefaultRepository;
    private final FaqSeedRepository faqSeedRepository;
    private final PublicWebsiteSlugReservationRepository publicSlugRepository;
    private final WhatsAppMetadataRepository whatsAppRepository;
    private final ClinicOnboardingValidationService validationService;
    private final ClinicOnboardingConflictService conflictService;
    private final ClinicOnboardingMapper mapper;
    private final TemporaryCredentialService temporaryCredentialService;
    private final ClinicAdminProvisioningService adminProvisioningService;
    private final DefaultFaqSeedProvider faqSeedProvider;
    private final CredentialEncryptor credentialEncryptor;
    private final SuperAdminIdempotencyService idempotencyService;
    private final ClinicOnboardingAuditService auditService;
    private final ObjectMapper objectMapper;

    public ClinicOnboardingService(
            ClinicRepository clinicRepository,
            ClinicSettingsRepository settingsRepository,
            OperatingHourRepository operatingHourRepository,
            ReminderDefaultRepository reminderDefaultRepository,
            FaqSeedRepository faqSeedRepository,
            PublicWebsiteSlugReservationRepository publicSlugRepository,
            WhatsAppMetadataRepository whatsAppRepository,
            ClinicOnboardingValidationService validationService,
            ClinicOnboardingConflictService conflictService,
            ClinicOnboardingMapper mapper,
            TemporaryCredentialService temporaryCredentialService,
            ClinicAdminProvisioningService adminProvisioningService,
            DefaultFaqSeedProvider faqSeedProvider,
            CredentialEncryptor credentialEncryptor,
            SuperAdminIdempotencyService idempotencyService,
            ClinicOnboardingAuditService auditService,
            ObjectMapper objectMapper) {
        this.clinicRepository = clinicRepository;
        this.settingsRepository = settingsRepository;
        this.operatingHourRepository = operatingHourRepository;
        this.reminderDefaultRepository = reminderDefaultRepository;
        this.faqSeedRepository = faqSeedRepository;
        this.publicSlugRepository = publicSlugRepository;
        this.whatsAppRepository = whatsAppRepository;
        this.validationService = validationService;
        this.conflictService = conflictService;
        this.mapper = mapper;
        this.temporaryCredentialService = temporaryCredentialService;
        this.adminProvisioningService = adminProvisioningService;
        this.faqSeedProvider = faqSeedProvider;
        this.credentialEncryptor = credentialEncryptor;
        this.idempotencyService = idempotencyService;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ClinicOnboardingResponse onboard(UserPrincipal actor, ClinicOnboardingRequest request, String idempotencyKeyHeader) {
        validationService.validate(request);
        String fingerprint = fingerprint(request);
        String idempotencyKey = IdempotencyKey.of(idempotencyKeyHeader).value();
        var existingReplay = idempotencyService.find(idempotencyKey, fingerprint);
        if (existingReplay.isPresent() && "SUCCEEDED".equals(existingReplay.get().getStatus())) {
            Clinic clinic = clinicRepository.findById(existingReplay.get().getClinicId()).orElseThrow();
            return new ClinicOnboardingResponse(clinic.getId(), clinic.getSlug(), null, null, null, null, true, true);
        }
        conflictService.validate(request);
        OnboardingRequestRecord record = existingReplay.orElseGet(() -> idempotencyService.start(idempotencyKey, fingerprint, actor.getUserId()));

        TemporaryCredentialService.IssuedTemporaryCredential credential = temporaryCredentialService.issue();
        Clinic clinic = clinicRepository.save(mapper.clinic(request));
        record.setClinicId(clinic.getId());

        createSettings(clinic, request);
        createOperatingHours(clinic, request);
        createReminders(clinic, request);
        faqSeedRepository.saveAll(faqSeedProvider.create(clinic.getId()));
        createPublicSlug(clinic, request);
        createWhatsApp(clinic, request);
        User admin = adminProvisioningService.createFirstAdmin(clinic.getId(), request.firstAdmin(), credential.passwordHash());
        adminProvisioningService.assignDefaultPermissions(clinic.getId(), admin.getId());

        auditService.record(actor, AuditEventType.CLINIC_CREATED, clinic.getId(), "Clinic", clinic.getId(), Map.of("slug", clinic.getSlug()));
        auditService.record(actor, AuditEventType.FIRST_CLINIC_ADMIN_CREATED, clinic.getId(), "User", admin.getId(), Map.of("email", admin.getEmail()));
        auditService.record(actor, AuditEventType.DEFAULT_PERMISSIONS_ASSIGNED, clinic.getId(), "User", admin.getId(), Map.of("role", "CLINIC_ADMIN"));
        auditService.record(actor, AuditEventType.WHATSAPP_METADATA_CAPTURED, clinic.getId(), "WhatsAppMetadata", clinic.getId(), Map.of("phoneNumberId", request.whatsapp().phoneNumberId()));
        auditService.record(actor, AuditEventType.PUBLIC_WEBSITE_SLUG_RESERVED, clinic.getId(), "PublicWebsiteSlugReservation", clinic.getId(), Map.of("slug", request.publicWebsite().slug()));
        auditService.record(actor, AuditEventType.TEMPORARY_CREDENTIAL_ISSUED, clinic.getId(), "User", admin.getId(), Map.of("temporaryPassword", "[issued]"));
        auditService.record(actor, AuditEventType.CLINIC_ONBOARDING_SUCCEEDED, clinic.getId(), "Clinic", clinic.getId(), Map.of("clinicSlug", clinic.getSlug()));
        idempotencyService.succeed(record, clinic.getId());

        return new ClinicOnboardingResponse(clinic.getId(), clinic.getSlug(), request.publicWebsite().slug(),
                admin.getId(), admin.getEmail(), credential.temporaryPassword(), true, true);
    }

    private String fingerprint(ClinicOnboardingRequest request) {
        try {
            return RequestFingerprint.from(objectMapper.writeValueAsString(request)).value();
        } catch (Exception ex) {
            return RequestFingerprint.from(request.toString()).value();
        }
    }

    private void createSettings(Clinic clinic, ClinicOnboardingRequest request) {
        ClinicSettings settings = new ClinicSettings();
        settings.setClinicId(clinic.getId());
        settings.setSlotDurationMinutes(request.settings().slotDurationMinutes());
        settings.setMaxAdvanceBookingDays(request.settings().maxAdvanceBookingDays());
        settings.setMinBookingNoticeMinutes(request.settings().minBookingNoticeMinutes());
        settings.setCancellationCutoffMinutes(request.settings().cancellationCutoffMinutes());
        settings.setRescheduleCutoffMinutes(request.settings().rescheduleCutoffMinutes());
        settings.setDefaultLanguage(request.clinic().defaultLanguage());
        settings.setPublicWebsiteEnabled(request.publicWebsite().enabled());
        settingsRepository.save(settings);
    }

    private void createOperatingHours(Clinic clinic, ClinicOnboardingRequest request) {
        for (ClinicOnboardingRequest.OperatingHourInput input : request.operatingHours()) {
            OperatingHour hour = new OperatingHour();
            hour.setClinicId(clinic.getId());
            hour.setDayOfWeek(input.dayOfWeek());
            hour.setClosed(input.closed());
            if (!input.closed()) {
                hour.setOpenTime(LocalTime.parse(input.openTime()));
                hour.setCloseTime(LocalTime.parse(input.closeTime()));
            }
            operatingHourRepository.save(hour);
        }
    }

    private void createReminders(Clinic clinic, ClinicOnboardingRequest request) {
        ReminderDefault morning = new ReminderDefault();
        morning.setClinicId(clinic.getId());
        morning.setType("MORNING_OF");
        morning.setEnabled(request.reminders().morningOfEnabled() == null || request.reminders().morningOfEnabled());
        morning.setSendTimeLocal(LocalTime.parse(request.reminders().morningOfTimeLocal() == null ? "08:00" : request.reminders().morningOfTimeLocal()));
        reminderDefaultRepository.save(morning);
        ReminderDefault before = new ReminderDefault();
        before.setClinicId(clinic.getId());
        before.setType("BEFORE_APPOINTMENT");
        before.setEnabled(request.reminders().beforeAppointmentEnabled() == null || request.reminders().beforeAppointmentEnabled());
        before.setOffsetMinutes(request.reminders().beforeAppointmentOffsetMinutes() == null ? 120 : request.reminders().beforeAppointmentOffsetMinutes());
        reminderDefaultRepository.save(before);
    }

    private void createPublicSlug(Clinic clinic, ClinicOnboardingRequest request) {
        PublicWebsiteSlugReservation reservation = new PublicWebsiteSlugReservation();
        reservation.setClinicId(clinic.getId());
        reservation.setSlug(request.publicWebsite().slug());
        reservation.setEnabled(request.publicWebsite().enabled());
        publicSlugRepository.save(reservation);
    }

    private void createWhatsApp(Clinic clinic, ClinicOnboardingRequest request) {
        WhatsAppMetadata metadata = new WhatsAppMetadata();
        metadata.setClinicId(clinic.getId());
        metadata.setWabaId(request.whatsapp().wabaId());
        metadata.setPhoneNumberId(request.whatsapp().phoneNumberId());
        metadata.setDisplayPhoneNumber(request.whatsapp().displayPhoneNumber());
        metadata.setTemplateNamespace(request.whatsapp().templateNamespace());
        metadata.setEncryptedAccessTokenPlaceholder(credentialEncryptor.encrypt(request.whatsapp().accessTokenPlaceholder()));
        metadata.setEncryptedAppSecretPlaceholder(credentialEncryptor.encrypt(request.whatsapp().appSecretPlaceholder()));
        whatsAppRepository.save(metadata);
    }
}
