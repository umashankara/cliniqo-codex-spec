package com.cliniqo.support;

import com.cliniqo.superadmin.dto.ClinicOnboardingRequest;
import java.util.List;

public final class F02Fixtures {
    private F02Fixtures() {
    }

    public static ClinicOnboardingRequest onboardingRequest(String slug, String adminEmail, String phoneNumberId, String displayPhone) {
        return new ClinicOnboardingRequest(
                new ClinicOnboardingRequest.ClinicProfileInput("Care Clinic", slug, "Main Road", "IN", "Asia/Kolkata", "en", "+919999999999", "clinic@example.com", null),
                new ClinicOnboardingRequest.ClinicSettingsInput(30, 30, 60, 120, 120),
                List.of(new ClinicOnboardingRequest.OperatingHourInput("MONDAY", false, "09:00", "17:00")),
                new ClinicOnboardingRequest.ReminderDefaultsInput(true, "08:00", true, 120),
                new ClinicOnboardingRequest.PublicWebsiteInput(slug, false),
                new ClinicOnboardingRequest.WhatsAppMetadataInput("waba", phoneNumberId, displayPhone, "care", "token", "secret"),
                new ClinicOnboardingRequest.FirstClinicAdminInput("Admin User", adminEmail, "+919999999998"));
    }
}
