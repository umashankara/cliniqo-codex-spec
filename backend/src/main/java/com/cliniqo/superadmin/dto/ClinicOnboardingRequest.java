package com.cliniqo.superadmin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ClinicOnboardingRequest(
        @Valid @NotNull ClinicProfileInput clinic,
        @Valid @NotNull ClinicSettingsInput settings,
        @Valid @NotEmpty List<OperatingHourInput> operatingHours,
        @Valid @NotNull ReminderDefaultsInput reminders,
        @Valid @NotNull PublicWebsiteInput publicWebsite,
        @Valid @NotNull WhatsAppMetadataInput whatsapp,
        @Valid @NotNull FirstClinicAdminInput firstAdmin) {

    public record ClinicProfileInput(
            @NotBlank @Size(min = 2, max = 160) String name,
            @NotBlank String slug,
            @Size(max = 500) String address,
            @NotBlank String country,
            @NotBlank String timezone,
            @NotBlank String defaultLanguage,
            @NotBlank String primaryPhone,
            @Email @NotBlank String email,
            Object logoMetadata) {}

    public record ClinicSettingsInput(
            int slotDurationMinutes,
            int maxAdvanceBookingDays,
            int minBookingNoticeMinutes,
            int cancellationCutoffMinutes,
            int rescheduleCutoffMinutes) {}

    public record OperatingHourInput(
            @NotBlank String dayOfWeek,
            boolean closed,
            String openTime,
            String closeTime) {}

    public record ReminderDefaultsInput(
            Boolean morningOfEnabled,
            String morningOfTimeLocal,
            Boolean beforeAppointmentEnabled,
            Integer beforeAppointmentOffsetMinutes) {}

    public record PublicWebsiteInput(@NotBlank String slug, boolean enabled) {}

    public record WhatsAppMetadataInput(
            @NotBlank @Size(max = 120) String wabaId,
            @NotBlank @Size(max = 120) String phoneNumberId,
            @NotBlank @Size(max = 32) String displayPhoneNumber,
            @NotBlank @Size(max = 120) String templateNamespace,
            String accessTokenPlaceholder,
            String appSecretPlaceholder) {}

    public record FirstClinicAdminInput(
            @NotBlank @Size(min = 2, max = 160) String fullName,
            @Email @NotBlank String email,
            @Size(max = 32) String phone) {}
}
