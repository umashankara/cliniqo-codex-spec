package com.cliniqo.superadmin.dto;

import java.util.UUID;

public record DeactivateClinicResponse(UUID clinicId, String status, int revokedRefreshSessionCount, boolean alreadyInactive) {
}
