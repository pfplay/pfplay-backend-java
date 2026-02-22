package com.pfplaybackend.api.admin.adapter.in.web;

import com.pfplaybackend.api.admin.application.service.AdminUserService;
import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
import com.pfplaybackend.api.user.domain.value.AvatarSetting;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@Import(AdminUserControllerTest.TestMethodSecurityConfig.class)
class AdminUserControllerTest {

    @EnableMethodSecurity
    static class TestMethodSecurityConfig {}

    @Autowired MockMvc mockMvc;
    @MockBean AdminUserService adminUserService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("createVirtualMember — FM 권한이면 201 Created")
    void createVirtualMember_withFmAuthority_returns201() throws Exception {
        // given
        String body = """
                {"nickname": "TestUser", "avatarBodyUri": "https://example.com/body.png"}""";

        MemberData member = mockMemberData();
        when(adminUserService.createVirtualMember(any(), any(), any())).thenReturn(member);

        // when & then
        mockMvc.perform(post("/api/v1/admin/users/virtual")
                        .with(jwt().authorities(() -> "FM"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("createVirtualMember — 인증 없으면 401")
    void createVirtualMember_unauthenticated_returns401() throws Exception {
        // given
        String body = """
                {"nickname": "TestUser", "avatarBodyUri": "https://example.com/body.png"}""";

        // when & then
        mockMvc.perform(post("/api/v1/admin/users/virtual")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getVirtualMember — FM 권한이면 200 OK")
    void getVirtualMember_withFmAuthority_returns200() throws Exception {
        // given
        MemberData member = mockMemberData();
        when(adminUserService.getVirtualMember(any())).thenReturn(member);

        // when & then
        mockMvc.perform(get("/api/v1/admin/users/virtual/1")
                        .with(jwt().authorities(() -> "FM")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("deleteVirtualMember — FM 권한이면 204 No Content")
    void deleteVirtualMember_withFmAuthority_returns204() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/admin/users/virtual/1")
                        .with(jwt().authorities(() -> "FM"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    private MemberData mockMemberData() {
        MemberData member = mock(MemberData.class);
        ProfileData profileData = mock(ProfileData.class);
        AvatarSetting avatarSetting = mock(AvatarSetting.class);

        when(member.getUserId()).thenReturn(new UserId(1L));
        when(member.getEmail()).thenReturn("test@pfplay.system");
        when(member.getProviderType()).thenReturn(ProviderType.ADMIN);
        when(member.getAuthorityTier()).thenReturn(AuthorityTier.FM);
        when(member.getProfileData()).thenReturn(profileData);
        when(member.getCreatedAt()).thenReturn(null);
        when(member.getUpdatedAt()).thenReturn(null);

        when(profileData.getNicknameValue()).thenReturn("TestUser");
        when(profileData.getIntroduction()).thenReturn("");
        when(profileData.getAvatarSetting()).thenReturn(avatarSetting);

        when(avatarSetting.getAvatarBodyUri()).thenReturn(new AvatarBodyUri("body-uri"));
        when(avatarSetting.getAvatarFaceUri()).thenReturn(new AvatarFaceUri());
        when(avatarSetting.getAvatarIconUri()).thenReturn(new AvatarIconUri());
        when(avatarSetting.getAvatarCompositionType()).thenReturn(AvatarCompositionType.SINGLE_BODY);
        when(avatarSetting.getCombinePositionX()).thenReturn(0);
        when(avatarSetting.getCombinePositionY()).thenReturn(0);
        when(avatarSetting.getOffsetX()).thenReturn(0.0);
        when(avatarSetting.getOffsetY()).thenReturn(0.0);
        when(avatarSetting.getScale()).thenReturn(0.0);

        return member;
    }
}
