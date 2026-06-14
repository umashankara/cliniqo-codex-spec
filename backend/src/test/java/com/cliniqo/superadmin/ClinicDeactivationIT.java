package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cliniqo.common.enums.ClinicStatus;
import org.junit.jupiter.api.Test;

class ClinicDeactivationIT {
    @Test
    void inactiveStatusExists() {
        assertEquals("INACTIVE", ClinicStatus.INACTIVE.name());
    }
}
