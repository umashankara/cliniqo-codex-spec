package com.cliniqo.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cliniqo.auth.entity.RefreshSession;
import com.cliniqo.auth.entity.User;
import com.cliniqo.auth.repository.RefreshSessionRepository;
import com.cliniqo.auth.repository.UserRepository;
import com.cliniqo.auth.service.RefreshSessionService;
import com.cliniqo.common.enums.UserStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cliniqo.support.PostgresIntegrationTest;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class RefreshSessionIT extends PostgresIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RefreshSessionRepository refreshSessionRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void refreshRotatesTokenAndReuseRevokesTheFamily() throws Exception {
        JsonNode login = login();
        String refreshToken = login.at("/data/refreshToken").asText();

        String refreshJson = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andReturn().getResponse().getContentAsString();
        String rotated = objectMapper.readTree(refreshJson).at("/data/refreshToken").asText();

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + rotated + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredRefreshSessionIsRejected() throws Exception {
        String refreshToken = login().at("/data/refreshToken").asText();
        RefreshSession session = refreshSessionRepository.findByTokenHash(RefreshSessionService.hash(refreshToken))
                .orElseThrow();
        session.setExpiresAt(Instant.now().minusSeconds(60));
        refreshSessionRepository.saveAndFlush(session);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deactivatedUserCannotRefreshSession() throws Exception {
        String refreshToken = login().at("/data/refreshToken").asText();
        User user = userRepository.findByEmailIgnoreCase("admin-a@cliniqo.test").orElseThrow();
        user.setStatus(UserStatus.SUSPENDED);
        userRepository.saveAndFlush(user);

        try {
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                    .andExpect(status().isUnauthorized());
        } finally {
            User restored = userRepository.findByEmailIgnoreCase("admin-a@cliniqo.test").orElseThrow();
            restored.setStatus(UserStatus.ACTIVE);
            userRepository.saveAndFlush(restored);
        }
    }

    @Test
    void logoutRevokesRefreshSession() throws Exception {
        String refreshToken = login().at("/data/refreshToken").asText();

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    private JsonNode login() throws Exception {
        String loginJson = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin-a@cliniqo.test\",\"password\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(loginJson);
    }
}
