package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.cliniqo.common.validation.SlugValidator;
import org.junit.jupiter.api.Test;

class SlugCanonicalizationIT {
    @Test
    void acceptsCanonicalSlugExactlyAsSubmitted() {
        assertDoesNotThrow(() -> SlugValidator.requireCanonical("clinic.slug", "care-clinic"));
    }
}
