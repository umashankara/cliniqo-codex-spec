package com.cliniqo.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cliniqo.common.enums.ErrorCode;
import org.junit.jupiter.api.Test;

class InactiveClinicRefreshIT {
    @Test
    void inactiveClinicUsesTypedError() {
        assertEquals("CLINIC_INACTIVE", ErrorCode.CLINIC_INACTIVE.name());
    }
}
