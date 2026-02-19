package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyroomInfoServiceIsRegisteredTest {

    @InjectMocks
    private PartyroomInfoService partyroomInfoService;

    private UserId myUserId;
    private UserId otherUserId;

    @BeforeEach
    void setUp() {
        myUserId = new UserId();
        otherUserId = new UserId();

        AuthContext authContext = mock(AuthContext.class);
        when(authContext.getUserId()).thenReturn(myUserId);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("DJ 2명 이상일 때 내가 등록되어 있으면 true 반환 (anyMatch)")
    void isAlreadyRegistered_shouldReturnTrue_whenMyUserIsAmongMultipleDjs() {
        // given
        DjData myDj = DjData.builder()
                .id(100L)
                .userId(myUserId)
                .crewId(new CrewId(1L))
                .playlistId(new PlaylistId(1L))
                .orderNumber(1)
                .isQueued(true)
                .build();

        DjData otherDj = DjData.builder()
                .id(200L)
                .userId(otherUserId)
                .crewId(new CrewId(2L))
                .playlistId(new PlaylistId(2L))
                .orderNumber(2)
                .isQueued(true)
                .build();

        Set<DjData> djSet = new HashSet<>();
        djSet.add(myDj);
        djSet.add(otherDj);

        PartyroomData partyroom = PartyroomData.builder()
                .id(1L)
                .partyroomId(new PartyroomId(1L))
                .build();
        partyroom.assignDjDataSet(djSet);

        // when
        boolean result = partyroomInfoService.isAlreadyRegistered(partyroom);

        // then — allMatch라면 false가 반환됨 (버그), anyMatch라면 true
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("내가 등록되어 있지 않으면 false 반환")
    void isAlreadyRegistered_shouldReturnFalse_whenMyUserIsNotRegistered() {
        // given
        DjData otherDj = DjData.builder()
                .id(200L)
                .userId(otherUserId)
                .crewId(new CrewId(2L))
                .playlistId(new PlaylistId(2L))
                .orderNumber(1)
                .isQueued(true)
                .build();

        Set<DjData> djSet = new HashSet<>();
        djSet.add(otherDj);

        PartyroomData partyroom = PartyroomData.builder()
                .id(1L)
                .partyroomId(new PartyroomId(1L))
                .build();
        partyroom.assignDjDataSet(djSet);

        // when
        boolean result = partyroomInfoService.isAlreadyRegistered(partyroom);

        // then
        assertThat(result).isFalse();
    }
}
