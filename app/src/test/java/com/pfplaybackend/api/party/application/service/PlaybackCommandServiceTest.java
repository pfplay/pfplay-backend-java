package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.Duration;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackAggregationRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackRepository;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.port.out.PlaylistCommandPort;
import com.pfplaybackend.api.party.application.port.out.UserActivityPort;
import com.pfplaybackend.api.party.application.service.task.ExpirationTaskScheduler;
import com.pfplaybackend.api.common.domain.value.PlaylistId;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.value.PlaybackTimeLimit;
import com.pfplaybackend.api.playlist.application.dto.PlaybackTrackDto;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.service.PartyroomAggregateService;
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

import com.pfplaybackend.api.party.domain.entity.data.PlaybackAggregationData;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaybackCommandServiceTest {

    @Mock PlaybackRepository playbackRepository;
    @Mock PlaybackAggregationRepository playbackAggregationRepository;
    @Mock PlaylistCommandPort playlistCommandPort;
    @Mock UserActivityPort userActivityPort;
    @Mock ApplicationEventPublisher eventPublisher;
    @Mock PartyroomAggregatePort aggregatePort;
    @Mock ExpirationTaskScheduler scheduleService;
    @Mock PartyroomAggregateService partyroomAggregateService;
    @Mock PartyroomQueryService partyroomQueryService;

    @InjectMocks PlaybackCommandService playbackCommandService;

    private final UserId userId = new UserId(1L);
    private final PartyroomId partyroomId = new PartyroomId(10L);

    @BeforeEach
    void setUp() {
        AuthContext authContext = mock(AuthContext.class);
        lenient().when(authContext.getUserId()).thenReturn(userId);
        lenient().when(authContext.getAuthorityTier()).thenReturn(AuthorityTier.FM);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    @Test
    @DisplayName("complete — 완료 시 DJ 점수가 1 증가한다")
    void complete_updatesDjScore() {
        // given — tryProceed에서 DJ가 없으면 deactivate
        PartyroomData partyroom = PartyroomData.builder().id(partyroomId.getId()).partyroomId(partyroomId).build();
        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(partyroomAggregateService.hasQueuedDjs(partyroomId)).thenReturn(false);

        // when
        playbackCommandService.complete(partyroomId, userId);

        // then
        verify(userActivityPort).updateDjPointScore(userId, 1);
    }

    @Test
    @DisplayName("complete — 대기열에 DJ가 없으면 재생이 비활성화된다")
    void complete_noDjs_deactivates() {
        // given
        PartyroomData partyroom = PartyroomData.builder().id(partyroomId.getId()).partyroomId(partyroomId).build();
        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(partyroomAggregateService.hasQueuedDjs(partyroomId)).thenReturn(false);

        // when
        playbackCommandService.complete(partyroomId, userId);

        // then
        verify(partyroomAggregateService).deactivatePlayback(partyroomId);
    }

    @Test
    @DisplayName("skipByManager — MODERATOR 이상 등급이면 스킵이 실행된다")
    void skipByManager_moderator_succeeds() {
        // given
        ActivePartyroomDto activeDto = new ActivePartyroomDto(partyroomId.getId(), false, 1L, true, new PlaybackId(1L), new CrewId(1L));
        CrewData adjuster = CrewData.builder()
                .id(1L).userId(userId).gradeType(GradeType.MODERATOR).build();
        PartyroomData partyroom = PartyroomData.builder().id(partyroomId.getId()).partyroomId(partyroomId).build();

        when(partyroomQueryService.getMyActivePartyroom(userId)).thenReturn(Optional.of(activeDto));
        when(partyroomQueryService.getCrewOrThrow(new PartyroomId(activeDto.id()), userId)).thenReturn(adjuster);
        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(partyroomAggregateService.hasQueuedDjs(partyroomId)).thenReturn(false);

        // when
        playbackCommandService.skipByManager(partyroomId);

        // then
        verify(scheduleService).deleteKey(String.valueOf(partyroomId.getId()));
        verify(partyroomAggregateService).deactivatePlayback(partyroomId);
    }

    @Test
    @DisplayName("skipByManager — MODERATOR 미만 등급이면 예외가 발생한다")
    void skipByManager_belowModerator_throws() {
        // given
        ActivePartyroomDto activeDto = new ActivePartyroomDto(partyroomId.getId(), false, 1L, true, new PlaybackId(1L), new CrewId(1L));
        CrewData adjuster = CrewData.builder()
                .id(1L).userId(userId).gradeType(GradeType.CLUBBER).build();

        when(partyroomQueryService.getMyActivePartyroom(userId)).thenReturn(Optional.of(activeDto));
        when(partyroomQueryService.getCrewOrThrow(new PartyroomId(activeDto.id()), userId)).thenReturn(adjuster);

        // when & then
        assertThatThrownBy(() -> playbackCommandService.skipByManager(partyroomId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("skipBySystem — 스케줄 태스크를 취소하고 tryProceed를 실행한다")
    void skipBySystem_cancelsTaskAndProceeds() {
        // given
        PartyroomData partyroom = PartyroomData.builder().id(partyroomId.getId()).partyroomId(partyroomId).build();
        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(partyroomAggregateService.hasQueuedDjs(partyroomId)).thenReturn(false);

        // when
        playbackCommandService.skipBySystem(partyroomId);

        // then
        verify(scheduleService).deleteKey(String.valueOf(partyroomId.getId()));
        verify(partyroomAggregateService).deactivatePlayback(partyroomId);
    }

    @Test
    @DisplayName("complete — 대기열에 DJ가 있으면 재생이 시작된다")
    void complete_hasDjs_startsPlayback() {
        // given
        PartyroomData partyroom = PartyroomData.builder()
                .id(partyroomId.getId()).partyroomId(partyroomId)
                .playbackTimeLimit(PlaybackTimeLimit.ofMinutes(10))
                .build();
        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(partyroomAggregateService.hasQueuedDjs(partyroomId)).thenReturn(true);

        PlaylistId playlistId = new PlaylistId(100L);
        DjData djData = DjData.builder()
                .id(1L).partyroomId(partyroomId).crewId(new CrewId(1L))
                .playlistId(playlistId).orderNumber(1).build();
        when(aggregatePort.findDjsOrdered(partyroomId)).thenReturn(List.of(djData));

        CrewData djCrew = CrewData.builder()
                .id(1L).partyroomId(partyroomId).userId(userId).gradeType(GradeType.CLUBBER).build();
        when(aggregatePort.findCrewById(1L)).thenReturn(Optional.of(djCrew));

        PlaybackTrackDto trackDto = new PlaybackTrackDto("linkId", "Song", "thumb.jpg", "3:30", 1);
        when(playlistCommandPort.getFirstTrack(playlistId)).thenReturn(trackDto);

        PlaybackData savedPlayback = mock(PlaybackData.class);
        lenient().when(savedPlayback.getId()).thenReturn(1L);
        lenient().when(savedPlayback.getLinkId()).thenReturn("linkId");
        lenient().when(savedPlayback.getName()).thenReturn("Song");
        lenient().when(savedPlayback.getDuration()).thenReturn(Duration.ofSeconds(210));
        lenient().when(savedPlayback.getThumbnailImage()).thenReturn("thumb.jpg");
        lenient().when(savedPlayback.getEndTime()).thenReturn(9999L);
        when(playbackRepository.save(any(PlaybackData.class))).thenReturn(savedPlayback);

        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(partyroomId);
        when(aggregatePort.findPlaybackState(partyroomId)).thenReturn(playbackState);

        // when
        playbackCommandService.complete(partyroomId, userId);

        // then
        verify(partyroomAggregateService).rotateDjQueue(partyroomId);
        verify(userActivityPort).updateDjPointScore(userId, 1);
    }

    @Test
    @DisplayName("updatePlaybackAggregation — 어그리게이션이 정상 업데이트된다")
    void updatePlaybackAggregation_updatesSuccessfully() {
        // given
        PlaybackAggregationData aggregation = mock(PlaybackAggregationData.class);
        PlaybackId playbackId = new PlaybackId(1L);
        when(playbackAggregationRepository.findById(playbackId)).thenReturn(Optional.of(aggregation));
        when(playbackAggregationRepository.save(any())).thenReturn(aggregation);

        // when
        playbackCommandService.updatePlaybackAggregation(playbackId, List.of(1, 0, 1));

        // then
        verify(aggregation).updateAggregation(1, 0, 1);
        verify(playbackAggregationRepository).save(aggregation);
    }
}
