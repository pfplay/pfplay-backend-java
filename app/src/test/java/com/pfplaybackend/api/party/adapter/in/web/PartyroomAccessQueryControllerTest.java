package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.party.application.port.out.GuestAuthPort;
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

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @MockBean GuestAuthPort guestAuthPort;
    @MockBean CookieUtil cookieUtil;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("enterPartyroomByLinkAddress — 인증된 사용자이면 200 OK")
    void enterPartyroomByLinkAddress_authenticated_returns200() throws Exception {
        // given
        when(partyroomAccessQueryService.getRedirectUri("test-link")).thenReturn(Map.of("partyroomId", 1L));

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms/link/test-link/enter")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("enterPartyroomByLinkAddress — 미인증 사용자도 200 OK")
    void enterPartyroomByLinkAddress_anonymous_returns200() throws Exception {
        // given
        when(guestAuthPort.getOrCreateGuestToken()).thenReturn("guest-token");
        when(partyroomAccessQueryService.getRedirectUri("test-link")).thenReturn(Map.of("partyroomId", 1L));

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms/link/test-link/enter")
                        .with(anonymous()))
                .andExpect(status().isOk());
    }
}
