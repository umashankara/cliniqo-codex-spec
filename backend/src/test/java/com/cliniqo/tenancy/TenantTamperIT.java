package com.cliniqo.tenancy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cliniqo.audit.entity.AuditEvent;
import com.cliniqo.audit.repository.AuditEventRepository;
import com.cliniqo.common.enums.AuditEventType;
import com.cliniqo.common.enums.UserRole;
import com.cliniqo.common.security.JwtTokenProvider;
import com.cliniqo.common.security.UserPrincipal;
import com.cliniqo.support.FoundationFixtures;
import com.cliniqo.support.PostgresIntegrationTest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class TenantTamperIT extends PostgresIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    AuditEventRepository auditEventRepository;

    @Test
    void clinicIdInBodyIsIgnoredAndAuditedForClinicUser() throws Exception {
        String requestId = "tenant-tamper-" + UUID.randomUUID();
        String token = tokenProvider.createAccessToken(new UserPrincipal(
                FoundationFixtures.CLINIC_A_ADMIN_ID,
                FoundationFixtures.CLINIC_A_ID,
                "admin-a@cliniqo.test",
                "",
                UserRole.CLINIC_ADMIN,
                true));

        mockMvc.perform(patch("/api/v1/foundation/tenant-probe/" + FoundationFixtures.CLINIC_A_PROBE_ID)
                        .header("Authorization", "Bearer " + token)
                        .header("X-Request-Id", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clinicId\":\"" + FoundationFixtures.CLINIC_B_ID + "\"}"))
                .andExpect(status().isOk());

        List<AuditEvent> events = auditEventRepository.findByRequestId(requestId);
        assertThat(events).hasSize(1);
        AuditEvent event = events.getFirst();
        assertThat(event.getEventType()).isEqualTo(AuditEventType.TENANT_TAMPER);
        assertThat(event.getActorUserId()).isEqualTo(FoundationFixtures.CLINIC_A_ADMIN_ID);
        assertThat(event.getClinicId()).isEqualTo(FoundationFixtures.CLINIC_A_ID);
        assertThat(event.getTargetId()).isEqualTo(FoundationFixtures.CLINIC_A_PROBE_ID);
        assertThat(event.getMetadata()).contains("[client supplied]");
        assertThat(event.getMetadata()).doesNotContain(FoundationFixtures.CLINIC_B_ID.toString());
    }
}
