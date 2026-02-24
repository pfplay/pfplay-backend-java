package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyroomQueryServiceIsRegisteredTest {

    @Mock private PartyroomAggregatePort aggregatePort;
    @InjectMocks
    private PartyroomQueryService partyroomQueryService;

    private UserId myUserId;

    @BeforeEach
    void setUp() {
        myUserId = new UserId();

        AuthContext authContext = mock(AuthContext.class);
        when(authContext.getUserId()).thenReturn(myUserId);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("DJ 대기열에 등록되어 있으면 true 반환")
    void isAlreadyRegisteredShouldReturnTrueWhenUserIsRegistered() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        CrewData crew = CrewData.builder().id(5L).userId(myUserId).build();
        when(aggregatePort.findCrew(partyroomId, myUserId))
                .thenReturn(Optional.of(crew));
        when(aggregatePort.isDjRegistered(partyroomId, new CrewId(5L)))
                .thenReturn(true);

        // when
        boolean result = partyroomQueryService.isAlreadyRegistered(partyroomId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("DJ 대기열에 등록되어 있지 않으면 false 반환")
    void isAlreadyRegisteredShouldReturnFalseWhenUserIsNotRegistered() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        CrewData crew = CrewData.builder().id(5L).userId(myUserId).build();
        when(aggregatePort.findCrew(partyroomId, myUserId))
                .thenReturn(Optional.of(crew));
        when(aggregatePort.isDjRegistered(partyroomId, new CrewId(5L)))
                .thenReturn(false);

        // when
        boolean result = partyroomQueryService.isAlreadyRegistered(partyroomId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("크루가 없으면 false 반환")
    void isAlreadyRegisteredShouldReturnFalseWhenCrewNotFound() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        when(aggregatePort.findCrew(partyroomId, myUserId))
                .thenReturn(Optional.empty());

        // when
        boolean result = partyroomQueryService.isAlreadyRegistered(partyroomId);

        // then
        assertThat(result).isFalse();
    }
}
