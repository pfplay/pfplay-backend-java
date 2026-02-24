package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.application.service.UserProfileQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileQueryController.class)
class UserProfileQueryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserProfileQueryService userProfileQueryService;

    @MockBean
    JwtDecoder jwtDecoder;

    @Test
    @DisplayName("GET /me/profile/summary — MEMBER 권한이면 200과 프로필 JSON을 반환한다")
    void getMyProfileSummaryMemberReturns200() throws Exception {
        // given
        ProfileSummaryDto dto = new ProfileSummaryDto(
                "TestNick", "Hello", "body.png",
                AvatarCompositionType.SINGLE_BODY,
                0, 0, 0.0, 0.0, 1.0,
                "face.png", "icon.png", "0xABC",
                List.of(new ActivitySummaryDto(
                        com.pfplaybackend.api.user.domain.enums.ActivityType.DJ_PNT, 100))
        );
        when(userProfileQueryService.getMyProfileSummary()).thenReturn(dto);

        // when & then
        mockMvc.perform(get("/api/v1/users/me/profile/summary")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("TestNick"))
                .andExpect(jsonPath("$.data.introduction").value("Hello"))
                .andExpect(jsonPath("$.data.avatarBodyUri").value("body.png"));
    }

    @Test
    @DisplayName("GET /me/profile/summary — GUEST 권한이면 200을 반환한다")
    void getMyProfileSummaryGuestReturns200() throws Exception {
        // given
        ProfileSummaryDto dto = new ProfileSummaryDto(
                "GuestNick", "", "body.png",
                AvatarCompositionType.SINGLE_BODY,
                0, 0, 0.0, 0.0, 1.0,
                null, "icon.png", null, List.of()
        );
        when(userProfileQueryService.getMyProfileSummary()).thenReturn(dto);

        // when & then
        mockMvc.perform(get("/api/v1/users/me/profile/summary")
                        .with(jwt().authorities(() -> "ROLE_GUEST")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /me/profile/summary — 미인증이면 401을 반환한다")
    void getMyProfileSummaryUnauthenticatedReturns401() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/users/me/profile/summary"))
                .andExpect(status().isUnauthorized());
    }
}
