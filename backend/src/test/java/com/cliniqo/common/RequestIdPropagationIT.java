package com.cliniqo.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cliniqo.support.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class RequestIdPropagationIT extends PostgresIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void preservesInboundRequestId() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .header("X-Request-Id", "req-test-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin-a@cliniqo.test\",\"password\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-Id", "req-test-1"))
                .andExpect(jsonPath("$.meta.requestId").value("req-test-1"));
    }
}
