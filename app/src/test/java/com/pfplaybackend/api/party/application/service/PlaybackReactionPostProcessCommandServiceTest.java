package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.model.ReactionPostProcessResult;
import com.pfplaybackend.api.party.application.port.out.PlaylistCommandPort;
import com.pfplaybackend.api.party.application.port.out.UserActivityPort;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackAggregationData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.MotionType;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.event.ReactionAggregationChangedEvent;
import com.pfplaybackend.api.party.domain.event.ReactionMotionChangedEvent;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaybackReactionPostProcessCommandServiceTest {

    @Mock PlaybackQueryService playbackQueryService;
    @Mock PlaybackCommandService playbackCommandService;
    @Mock ApplicationEventPublisher eventPublisher;
    @Mock PlaylistCommandPort playlistCommandPort;
    @Mock UserActivityPort userActivityPort;

    @InjectMocks PlaybackReactionPostProcessCommandService service;

    private final UserId userId = new UserId(1L);
    private final PartyroomId partyroomId = new PartyroomId(10L);
    private final PlaybackId playbackId = new PlaybackId(100L);
    private final CrewId crewId = new CrewId(5L);

    @BeforeEach
    void setUp() {
        AuthContext authContext = mock(AuthContext.class);
        lenient().when(authContext.getUserId()).thenReturn(userId);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("grab 상태 변경 시 grabTrack이 호출된다")
    void postProcess_grabStatusChanged_callsGrabTrack() {
        // given
        ReactionPostProcessResult dto = new ReactionPostProcessResult(
                false, false, false, true, null, 0, MotionType.NONE);

        PlaybackData playback = mock(PlaybackData.class);
        lenient().when(playback.getUserId()).thenReturn(new UserId(2L));
        when(playback.getLinkId()).thenReturn("link-123");
        when(playbackQueryService.getPlaybackById(playbackId)).thenReturn(playback);

        // when
        service.postProcess(dto, ReactionType.GRAB, partyroomId, playbackId, crewId);

        // then
        verify(playlistCommandPort).grabTrack(userId, "link-123");
        verify(eventPublisher).publishEvent(any(ReactionMotionChangedEvent.class));
    }

    @Test
    @DisplayName("DJ 활동 점수 변경 시 점수가 업데이트된다")
    void postProcess_djActivityScoreChanged_updatesScore() {
        // given
        ReactionPostProcessResult dto = new ReactionPostProcessResult(
                false, false, true, false, null, 5, MotionType.NONE);

        PlaybackData playback = mock(PlaybackData.class);
        when(playback.getUserId()).thenReturn(new UserId(2L));
        when(playbackQueryService.getPlaybackById(playbackId)).thenReturn(playback);

        // when
        service.postProcess(dto, ReactionType.LIKE, partyroomId, playbackId, crewId);

        // then
        verify(userActivityPort).updateDjPointScore(new UserId(2L), 5);
    }

    @Test
    @DisplayName("집계 변경 시 PlaybackAggregation이 업데이트되고 이벤트가 발행된다")
    void postProcess_aggregationChanged_updatesAndPublishes() {
        // given
        ReactionPostProcessResult dto = new ReactionPostProcessResult(
                true, false, false, false, List.of(1, 0, 0), 0, MotionType.NONE);

        PlaybackData playback = mock(PlaybackData.class);
        lenient().when(playback.getUserId()).thenReturn(new UserId(2L));
        when(playback.getId()).thenReturn(playbackId.getId());
        when(playbackQueryService.getPlaybackById(playbackId)).thenReturn(playback);

        PlaybackAggregationData aggregation = mock(PlaybackAggregationData.class);
        when(aggregation.getLikeCount()).thenReturn(5);
        when(aggregation.getDislikeCount()).thenReturn(1);
        when(aggregation.getGrabCount()).thenReturn(2);
        when(playbackCommandService.updatePlaybackAggregation(playbackId, List.of(1, 0, 0)))
                .thenReturn(aggregation);

        // when
        service.postProcess(dto, ReactionType.LIKE, partyroomId, playbackId, crewId);

        // then
        verify(playbackCommandService).updatePlaybackAggregation(playbackId, List.of(1, 0, 0));
        verify(eventPublisher).publishEvent(any(ReactionAggregationChangedEvent.class));
        verify(eventPublisher).publishEvent(any(ReactionMotionChangedEvent.class));
    }

    @Test
    @DisplayName("아무 상태도 변경되지 않으면 motion 이벤트만 발행된다")
    void postProcess_noChanges_onlyMotionEvent() {
        // given
        ReactionPostProcessResult dto = new ReactionPostProcessResult(
                false, false, false, false, null, 0, MotionType.NONE);

        PlaybackData playback = mock(PlaybackData.class);
        lenient().when(playback.getUserId()).thenReturn(new UserId(2L));
        when(playbackQueryService.getPlaybackById(playbackId)).thenReturn(playback);

        // when
        service.postProcess(dto, ReactionType.LIKE, partyroomId, playbackId, crewId);

        // then
        verify(playlistCommandPort, never()).grabTrack(any(), any());
        verify(userActivityPort, never()).updateDjPointScore(any(), anyInt());
        verify(eventPublisher, times(1)).publishEvent(any(ReactionMotionChangedEvent.class));
    }
}
