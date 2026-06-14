package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cliniqo.common.validation.SlugValidator;
import org.junit.jupiter.api.Test;

class ClinicSlugConflictIT {
    @Test
    void rejectsInvalidSlug() {
        assertThrows(RuntimeException.class, () -> SlugValidator.requireCanonical("clinic.slug", "Care Clinic"));
    }
}
