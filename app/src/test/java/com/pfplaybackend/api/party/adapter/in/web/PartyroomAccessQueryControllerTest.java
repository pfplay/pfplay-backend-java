package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.dto.partyroom.LinkEnterDto;
import com.pfplaybackend.api.party.application.service.PartyroomAccessQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartyroomAccessQueryController.class)
@Import(PartyroomAccessQueryControllerTest.TestSecurityConfig.class)
class PartyroomAccessQueryControllerTest {

    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(request -> request
                            .requestMatchers("/api/v1/partyrooms/link/**").permitAll()
                            .requestMatchers("/api/**").authenticated()
                    )
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));
            return http.build();
        }
    }

    @Autowired MockMvc mockMvc;
    @MockBean PartyroomAccessQueryService partyroomAccessQueryService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("getPartyroomByLink — 인증된 사용자이면 200 OK")
    void getPartyroomByLinkAuthenticatedReturns200() throws Exception {
        // given
        LinkEnterDto dto = new LinkEnterDto(1L, "Party Room", "Welcome!", null, 5);
        when(partyroomAccessQueryService.getPartyroomByLink("test-link")).thenReturn(dto);

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms/link/test-link")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.partyroomId").value(1))
                .andExpect(jsonPath("$.data.title").value("Party Room"))
                .andExpect(jsonPath("$.data.crewCount").value(5));
    }

    @Test
    @DisplayName("getPartyroomByLink — 미인증 사용자도 200 OK (permitAll)")
    void getPartyroomByLinkAnonymousReturns200() throws Exception {
        // given
        LinkEnterDto dto = new LinkEnterDto(1L, "Party Room", "Welcome!", null, 5);
        when(partyroomAccessQueryService.getPartyroomByLink("test-link")).thenReturn(dto);

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms/link/test-link")
                        .with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.playback").doesNotExist());
    }
}
