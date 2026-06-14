package com.cliniqo.superadmin.dto;

import java.util.UUID;

public record ClinicOnboardingResponse(
        UUID clinicId,
        String clinicSlug,
        String publicWebsiteSlug,
        UUID firstAdminUserId,
        String firstAdminEmail,
        String temporaryPassword,
        boolean forcePasswordReset,
        boolean oneTimeCredential) {
}
