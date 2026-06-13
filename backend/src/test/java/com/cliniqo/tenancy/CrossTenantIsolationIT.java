package com.cliniqo.tenancy;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cliniqo.common.enums.UserRole;
import com.cliniqo.common.security.JwtTokenProvider;
import com.cliniqo.common.security.UserPrincipal;
import com.cliniqo.support.FoundationFixtures;
import com.cliniqo.support.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class CrossTenantIsolationIT extends PostgresIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Test
    void clinicAUserCannotReadClinicBProbe() throws Exception {
        String token = clinicToken(FoundationFixtures.CLINIC_A_ADMIN_ID, FoundationFixtures.CLINIC_A_ID, "admin-a@cliniqo.test");

        mockMvc.perform(get("/api/v1/foundation/tenant-probe/" + FoundationFixtures.CLINIC_B_PROBE_ID)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    void clinicAUserListDoesNotInferClinicBProbeExists() throws Exception {
        String token = clinicToken(FoundationFixtures.CLINIC_A_ADMIN_ID, FoundationFixtures.CLINIC_A_ID, "admin-a@cliniqo.test");

        mockMvc.perform(get("/api/v1/foundation/tenant-probe")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].id", hasItem(FoundationFixtures.CLINIC_A_PROBE_ID.toString())))
                .andExpect(jsonPath("$.data[*].id", not(hasItem(FoundationFixtures.CLINIC_B_PROBE_ID.toString()))));
    }

    @Test
    void clinicAUserCannotUpdateClinicBProbe() throws Exception {
        String token = clinicToken(FoundationFixtures.CLINIC_A_ADMIN_ID, FoundationFixtures.CLINIC_A_ID, "admin-a@cliniqo.test");

        mockMvc.perform(patch("/api/v1/foundation/tenant-probe/" + FoundationFixtures.CLINIC_B_PROBE_ID)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"attempted cross clinic update\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    void clinicAUserCannotDeleteClinicBProbe() throws Exception {
        String clinicAToken = clinicToken(FoundationFixtures.CLINIC_A_ADMIN_ID, FoundationFixtures.CLINIC_A_ID, "admin-a@cliniqo.test");
        String clinicBToken = clinicToken(FoundationFixtures.CLINIC_B_ADMIN_ID, FoundationFixtures.CLINIC_B_ID, "admin-b@cliniqo.test");

        mockMvc.perform(delete("/api/v1/foundation/tenant-probe/" + FoundationFixtures.CLINIC_B_PROBE_ID)
                        .header("Authorization", "Bearer " + clinicAToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));

        mockMvc.perform(get("/api/v1/foundation/tenant-probe/" + FoundationFixtures.CLINIC_B_PROBE_ID)
                        .header("Authorization", "Bearer " + clinicBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(FoundationFixtures.CLINIC_B_PROBE_ID.toString()))
                .andExpect(jsonPath("$.data.clinicOwned", notNullValue()));
    }

    private String clinicToken(java.util.UUID userId, java.util.UUID clinicId, String email) {
        return tokenProvider.createAccessToken(new UserPrincipal(
                userId,
                clinicId,
                email,
                "",
                UserRole.CLINIC_ADMIN,
                true));
    }
}
