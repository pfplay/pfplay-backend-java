package com.pfplaybackend.api.admin.adapter.in.web;

import com.pfplaybackend.api.admin.application.dto.result.AdminPartyroomResult;
import com.pfplaybackend.api.admin.application.dto.result.BulkPreviewResult;
import com.pfplaybackend.api.admin.application.service.AdminPartyroomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminPartyroomController.class)
@Import(AdminPartyroomControllerTest.TestMethodSecurityConfig.class)
class AdminPartyroomControllerTest {

    @EnableMethodSecurity
    static class TestMethodSecurityConfig {}

    @Autowired MockMvc mockMvc;
    @MockBean AdminPartyroomService adminPartyroomService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("createPartyroom — FM 권한이면 201 Created")
    void createPartyroom_withFmAuthority_returns201() throws Exception {
        // given
        String body = """
                {"hostUserId": "100", "title": "Test Room", "introduction": "Hello", "linkDomain": "testdomain01", "playbackTimeLimit": 5}""";

        AdminPartyroomResult result = new AdminPartyroomResult(1L, "100", "Test Room", "Welcome", "testdomain01", 5, "GENERAL", true, null);
        when(adminPartyroomService.createPartyroomWithHost(any())).thenReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/admin/partyrooms")
                        .with(jwt().authorities(() -> "FM"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("createPartyroom — 인증 없으면 401")
    void createPartyroom_unauthenticated_returns401() throws Exception {
        // given
        String body = """
                {"hostUserId": "100", "title": "Test Room", "introduction": "Hello", "linkDomain": "testdomain01", "playbackTimeLimit": 5}""";

        // when & then
        mockMvc.perform(post("/api/v1/admin/partyrooms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("createBulkPreviewEnvironment — FM 권한이면 201 Created")
    void createBulkPreviewEnvironment_withFmAuthority_returns201() throws Exception {
        // given
        String body = """
                {"partyroomCount": 5, "usersPerRoom": 5, "titlePrefix": "Room", "introduction": "Hello", "linkDomainPrefix": "demo", "playbackTimeLimit": 5}""";

        BulkPreviewResult result = new BulkPreviewResult(5, 25, 1000L, List.of());
        when(adminPartyroomService.createBulkPreviewEnvironment(any())).thenReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/admin/partyrooms/bulk-preview")
                        .with(jwt().authorities(() -> "FM"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("createBulkPreviewEnvironment — 인증 없으면 401")
    void createBulkPreviewEnvironment_unauthenticated_returns401() throws Exception {
        // given
        String body = """
                {"partyroomCount": 5, "usersPerRoom": 5, "titlePrefix": "Room", "introduction": "Hello", "linkDomainPrefix": "demo", "playbackTimeLimit": 5}""";

        // when & then
        mockMvc.perform(post("/api/v1/admin/partyrooms/bulk-preview")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
