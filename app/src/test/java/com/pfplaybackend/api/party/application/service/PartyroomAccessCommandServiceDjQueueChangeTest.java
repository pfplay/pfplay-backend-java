package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.event.CrewAccessedEvent;
import com.pfplaybackend.api.party.domain.event.DjQueueChangedEvent;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import com.pfplaybackend.api.party.domain.service.PartyroomAggregateService;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyroomAccessCommandServiceDjQueueChangeTest {

    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private PartyroomAggregatePort aggregatePort;
    @Mock private PartyroomAggregateService partyroomAggregateService;
    @Mock private PartyroomQueryService partyroomQueryService;
    @Mock private PlaybackCommandService playbackCommandService;

    @InjectMocks
    private PartyroomAccessCommandService partyroomAccessCommandService;

    private UserId userId;
    private PartyroomId partyroomId;

    @BeforeEach
    void setUp() {
        userId = new UserId();
        partyroomId = new PartyroomId(1L);

        AuthContext authContext = mock(AuthContext.class);
        lenient().when(authContext.getUserId()).thenReturn(userId);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("exit - DJ 대기열에 있던 사용자가 퇴장하면 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void exit_userInDjQueue_shouldPublishDjQueueChangeEvent() {
        // given
        CrewData crew = CrewData.builder()
                .id(1L)
                .userId(userId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        PartyroomData partyroomData = PartyroomData.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .build();

        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(1L);
        playbackState.activate(new PlaybackId(1L), new CrewId(99L));

        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroomData);
        when(aggregatePort.findCrew(partyroomId, userId)).thenReturn(Optional.of(crew));
        when(aggregatePort.findDj(partyroomId, new CrewId(1L))).thenReturn(Optional.of(
                DjData.builder().id(100L).crewId(new CrewId(1L)).playlistId(new PlaylistId(10L)).orderNumber(2).build()
        ));
        when(aggregatePort.findPlaybackState(partyroomId.getId())).thenReturn(playbackState);

        // when
        partyroomAccessCommandService.exit(partyroomId);

        // then
        verify(partyroomAggregateService).removeDjFromQueue(partyroomId, new CrewId(1L));
        verify(eventPublisher).publishEvent(any(DjQueueChangedEvent.class));
        verify(eventPublisher).publishEvent(any(CrewAccessedEvent.class));
    }

    @Test
    @DisplayName("exit - DJ 대기열에 없던 사용자가 퇴장하면 DJ_QUEUE_CHANGE 이벤트가 발행되지 않아야 한다")
    void exit_userNotInDjQueue_shouldNotPublishDjQueueChangeEvent() {
        // given
        CrewData crew = CrewData.builder()
                .id(1L)
                .userId(userId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        PartyroomData partyroomData = PartyroomData.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .build();

        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(1L);

        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroomData);
        when(aggregatePort.findCrew(partyroomId, userId)).thenReturn(Optional.of(crew));
        when(aggregatePort.findDj(partyroomId, new CrewId(1L))).thenReturn(Optional.empty());
        when(aggregatePort.findPlaybackState(partyroomId.getId())).thenReturn(playbackState);

        // when
        partyroomAccessCommandService.exit(partyroomId);

        // then
        verify(eventPublisher, never()).publishEvent(any(DjQueueChangedEvent.class));
        verify(eventPublisher).publishEvent(any(CrewAccessedEvent.class));
    }

    @Test
    @DisplayName("expel - DJ 대기열에 있던 사용자가 강퇴되면 DJ_QUEUE_CHANGE 이벤트가 발행되어야 한다")
    void expel_userInDjQueue_shouldPublishDjQueueChangeEvent() {
        // given
        UserId targetUserId = new UserId();

        CrewData targetCrew = CrewData.builder()
                .id(2L)
                .userId(targetUserId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        PartyroomData partyroomData = PartyroomData.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .build();

        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(1L);
        playbackState.activate(new PlaybackId(1L), new CrewId(99L));

        when(aggregatePort.findDj(partyroomId, new CrewId(2L))).thenReturn(Optional.of(
                DjData.builder().id(100L).crewId(new CrewId(2L)).playlistId(new PlaylistId(10L)).orderNumber(2).build()
        ));
        when(aggregatePort.findPlaybackState(partyroomData.getId())).thenReturn(playbackState);

        // when
        partyroomAccessCommandService.expel(partyroomData, targetCrew, false);

        // then
        verify(partyroomAggregateService).removeDjFromQueue(partyroomId, new CrewId(2L));
        verify(eventPublisher).publishEvent(any(DjQueueChangedEvent.class));
        verify(eventPublisher).publishEvent(any(CrewAccessedEvent.class));
    }

    @Test
    @DisplayName("expel - DJ 대기열에 없던 사용자가 강퇴되면 DJ_QUEUE_CHANGE 이벤트가 발행되지 않아야 한다")
    void expel_userNotInDjQueue_shouldNotPublishDjQueueChangeEvent() {
        // given
        UserId targetUserId = new UserId();

        CrewData targetCrew = CrewData.builder()
                .id(2L)
                .userId(targetUserId)
                .gradeType(GradeType.LISTENER)
                .isActive(true)
                .build();

        PartyroomData partyroomData = PartyroomData.builder()
                .id(1L)
                .partyroomId(partyroomId)
                .build();

        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(1L);

        when(aggregatePort.findDj(partyroomId, new CrewId(2L))).thenReturn(Optional.empty());
        when(aggregatePort.findPlaybackState(partyroomData.getId())).thenReturn(playbackState);

        // when
        partyroomAccessCommandService.expel(partyroomData, targetCrew, false);

        // then
        verify(eventPublisher, never()).publishEvent(any(DjQueueChangedEvent.class));
        verify(eventPublisher).publishEvent(any(CrewAccessedEvent.class));
    }
}
