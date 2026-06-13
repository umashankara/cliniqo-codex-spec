package com.cliniqo.tenancy;

import static org.assertj.core.api.Assertions.assertThat;

import com.cliniqo.auth.dto.LoginRequest;
import com.cliniqo.auth.dto.RefreshRequest;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class ClinicIdContractTest {
    @Test
    void authContractsDoNotAcceptClinicId() {
        assertNoClinicId(LoginRequest.class);
        assertNoClinicId(RefreshRequest.class);
    }

    private void assertNoClinicId(Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            assertThat(field.getName()).isNotIn("clinicId", "clinic_id", "tenantId", "tenant_id");
        }
    }
}
