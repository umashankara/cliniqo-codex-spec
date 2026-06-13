package com.cliniqo.tenancy;

import static org.assertj.core.api.Assertions.assertThat;

import com.cliniqo.auth.dto.LoginRequest;
import com.cliniqo.auth.dto.RefreshRequest;
import org.junit.jupiter.api.Test;

class TenancyArchitectureTest {
    @Test
    void tenantScopedDtosDoNotExposeClinicId() {
        assertThat(LoginRequest.class.getDeclaredFields()).noneMatch(field -> field.getName().equals("clinicId"));
        assertThat(RefreshRequest.class.getDeclaredFields()).noneMatch(field -> field.getName().equals("clinicId"));
    }
}
