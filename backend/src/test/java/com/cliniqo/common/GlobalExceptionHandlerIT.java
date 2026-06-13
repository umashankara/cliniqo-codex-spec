package com.cliniqo.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cliniqo.support.FoundationFixtures;
import com.cliniqo.support.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class GlobalExceptionHandlerIT extends PostgresIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void unauthenticatedErrorUsesEnvelope() throws Exception {
        mockMvc.perform(get("/api/v1/foundation/tenant-probe/" + FoundationFixtures.CLINIC_A_PROBE_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("UNAUTHENTICATED"))
                .andExpect(jsonPath("$.meta.requestId").exists());
    }
}
