package com.pfplaybackend.api.auth.application.service;

import com.pfplaybackend.api.common.config.security.jwt.CustomJwtAuthenticationToken;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.service.PartyroomAccessService;
import com.pfplaybackend.api.party.application.service.PartyroomInfoService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock private PartyroomInfoService partyroomInfoService;
    @Mock private PartyroomAccessService partyroomAccessService;

    @InjectMocks
    private LogoutService logoutService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("활성 파티룸이 있으면 exit()을 호출해야 한다")
    void exitActivePartyroomIfPresent_shouldCallExit_whenActivePartyroomExists() {
        // given
        UserId userId = new UserId();
        setAuthentication(userId);

        ActivePartyroomWithCrewDto activePartyroom = mock(ActivePartyroomWithCrewDto.class);
        when(activePartyroom.id()).thenReturn(1L);
        when(partyroomInfoService.getMyActivePartyroomWithCrewId(userId)).thenReturn(Optional.of(activePartyroom));

        // when
        logoutService.exitActivePartyroomIfPresent();

        // then
        verify(partyroomAccessService, times(1)).exit(new PartyroomId(1L));
    }

    @Test
    @DisplayName("활성 파티룸이 없으면 exit()을 호출하지 않아야 한다")
    void exitActivePartyroomIfPresent_shouldNotCallExit_whenNoActivePartyroom() {
        // given
        UserId userId = new UserId();
        setAuthentication(userId);

        when(partyroomInfoService.getMyActivePartyroomWithCrewId(userId)).thenReturn(Optional.empty());

        // when
        logoutService.exitActivePartyroomIfPresent();

        // then
        verify(partyroomAccessService, never()).exit(any());
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 아무 작업도 수행하지 않아야 한다")
    void exitActivePartyroomIfPresent_shouldDoNothing_whenNotAuthenticated() {
        // given — no authentication set

        // when
        logoutService.exitActivePartyroomIfPresent();

        // then
        verify(partyroomInfoService, never()).getMyActivePartyroomWithCrewId(any());
        verify(partyroomAccessService, never()).exit(any());
    }

    private void setAuthentication(UserId userId) {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .claim("sub", userId.getUid().toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        CustomJwtAuthenticationToken token = new CustomJwtAuthenticationToken(
                jwt, Collections.emptyList(), userId, "test@test.com", AuthorityTier.FM, "google"
        );
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
