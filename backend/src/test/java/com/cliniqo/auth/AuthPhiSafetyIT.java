package com.cliniqo.auth;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cliniqo.support.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class AuthPhiSafetyIT extends PostgresIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void failedLoginDoesNotEchoPasswordOrHash() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin-a@cliniqo.test\",\"password\":\"VerySecret123!\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(not(containsString("VerySecret123!"))))
                .andExpect(content().string(not(containsString("$2"))));
    }
}
