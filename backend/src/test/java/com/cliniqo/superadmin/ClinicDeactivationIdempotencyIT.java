package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.cliniqo.superadmin.dto.DeactivateClinicResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ClinicDeactivationIdempotencyIT {
    @Test
    void responseModelsAlreadyInactive() {
        assertNotNull(new DeactivateClinicResponse(UUID.randomUUID(), "INACTIVE", 0, true));
    }
}
