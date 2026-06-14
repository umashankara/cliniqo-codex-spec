package com.cliniqo.superadmin;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cliniqo.common.validation.SlugValidator;
import org.junit.jupiter.api.Test;

class PublicWebsiteSlugConflictIT {
    @Test
    void rejectsInvalidPublicSlug() {
        assertThrows(RuntimeException.class, () -> SlugValidator.requireCanonical("publicWebsite.slug", "care_website"));
    }
}
