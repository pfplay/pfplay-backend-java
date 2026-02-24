package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.PlaylistId;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.party.application.port.out.PlaybackControlPort;
import com.pfplaybackend.api.party.application.port.out.PlaylistQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.DjQueueData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.service.PartyroomAggregateService;
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
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DjCommandServiceTest {

    @Mock PartyroomAggregatePort aggregatePort;
    @Mock PlaybackControlPort playbackControlPort;
    @Mock PlaylistQueryPort playlistQueryPort;
    @Mock ApplicationEventPublisher eventPublisher;
    @Mock PartyroomAggregateService partyroomAggregateService;
    @Mock PartyroomQueryService partyroomQueryService;

    @InjectMocks DjCommandService djCommandService;

    private final UserId userId = new UserId(1L);
    private final PartyroomId partyroomId = new PartyroomId(10L);
    private final PlaylistId playlistId = new PlaylistId(100L);

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
    @DisplayName("enqueueDj — 큐가 닫혀있으면 예외가 발생한다")
    void enqueueDjQueueClosedThrows() {
        // given
        PartyroomData partyroom = PartyroomData.builder()
                .id(partyroomId.getId()).partyroomId(partyroomId).build();
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(partyroomId);
        DjQueueData djQueue = DjQueueData.createFor(partyroomId);
        djQueue.close();

        CrewData crew = CrewData.builder()
                .id(1L).partyroomId(partyroomId).userId(userId).gradeType(GradeType.CLUBBER).build();

        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(aggregatePort.findPlaybackState(partyroomId)).thenReturn(playbackState);
        when(aggregatePort.findDjQueueState(partyroomId)).thenReturn(djQueue);
        when(partyroomQueryService.getCrewOrThrow(partyroomId, userId)).thenReturn(crew);

        // when & then
        assertThatThrownBy(() -> djCommandService.enqueueDj(partyroomId, playlistId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("enqueueDj — 이미 등록된 DJ이면 예외가 발생한다")
    void enqueueDjAlreadyRegisteredThrows() {
        // given
        PartyroomData partyroom = PartyroomData.builder()
                .id(partyroomId.getId()).partyroomId(partyroomId).build();
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(partyroomId);
        DjQueueData djQueue = DjQueueData.createFor(partyroomId);

        CrewData crew = CrewData.builder()
                .id(1L).partyroomId(partyroomId).userId(userId).gradeType(GradeType.CLUBBER).build();

        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(aggregatePort.findPlaybackState(partyroomId)).thenReturn(playbackState);
        when(aggregatePort.findDjQueueState(partyroomId)).thenReturn(djQueue);
        when(partyroomQueryService.getCrewOrThrow(partyroomId, userId)).thenReturn(crew);
        when(aggregatePort.isDjRegistered(partyroomId, new CrewId(1L))).thenReturn(true);
        when(playlistQueryPort.isEmptyPlaylist(playlistId.getId())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> djCommandService.enqueueDj(partyroomId, playlistId))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("enqueueDj — 빈 플레이리스트이면 예외가 발생한다")
    void enqueueDjEmptyPlaylistThrows() {
        // given
        PartyroomData partyroom = PartyroomData.builder()
                .id(partyroomId.getId()).partyroomId(partyroomId).build();
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(partyroomId);
        DjQueueData djQueue = DjQueueData.createFor(partyroomId);

        CrewData crew = CrewData.builder()
                .id(1L).partyroomId(partyroomId).userId(userId).gradeType(GradeType.CLUBBER).build();

        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(aggregatePort.findPlaybackState(partyroomId)).thenReturn(playbackState);
        when(aggregatePort.findDjQueueState(partyroomId)).thenReturn(djQueue);
        when(partyroomQueryService.getCrewOrThrow(partyroomId, userId)).thenReturn(crew);
        when(aggregatePort.isDjRegistered(partyroomId, new CrewId(1L))).thenReturn(false);
        when(playlistQueryPort.isEmptyPlaylist(playlistId.getId())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> djCommandService.enqueueDj(partyroomId, playlistId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("enqueueDj — 첫 번째 DJ 등록 시 재생이 시작된다")
    void enqueueDjFirstDjStartsPlayback() {
        // given
        PartyroomData partyroom = PartyroomData.builder()
                .id(partyroomId.getId()).partyroomId(partyroomId).build();
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(partyroomId);
        DjQueueData djQueue = DjQueueData.createFor(partyroomId);

        CrewData crew = CrewData.builder()
                .id(1L).partyroomId(partyroomId).userId(userId).gradeType(GradeType.CLUBBER).build();

        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(aggregatePort.findPlaybackState(partyroomId)).thenReturn(playbackState);
        when(aggregatePort.findDjQueueState(partyroomId)).thenReturn(djQueue);
        when(partyroomQueryService.getCrewOrThrow(partyroomId, userId)).thenReturn(crew);
        when(aggregatePort.isDjRegistered(partyroomId, new CrewId(1L))).thenReturn(false);
        when(playlistQueryPort.isEmptyPlaylist(playlistId.getId())).thenReturn(false);
        when(aggregatePort.findDjsOrdered(partyroomId)).thenReturn(Collections.emptyList());
        when(aggregatePort.saveDj(any(DjData.class))).thenReturn(mock(DjData.class));

        // when
        djCommandService.enqueueDj(partyroomId, playlistId);

        // then
        verify(playbackControlPort).startPlayback(partyroom);
    }

    @Test
    @DisplayName("dequeueDj — 현재 DJ가 아니면 skipBySystem이 호출되지 않는다")
    void dequeueDjNotCurrentDjNoSkip() {
        // given
        PartyroomData partyroom = PartyroomData.builder()
                .id(partyroomId.getId()).partyroomId(partyroomId).build();

        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(partyroomId);
        playbackState.activate(null, new CrewId(99L)); // different DJ

        CrewData crew = CrewData.builder()
                .id(1L).partyroomId(partyroomId).userId(userId).gradeType(GradeType.CLUBBER).build();

        when(partyroomQueryService.getPartyroomById(partyroomId)).thenReturn(partyroom);
        when(aggregatePort.findPlaybackState(partyroomId)).thenReturn(playbackState);
        when(partyroomQueryService.getCrewOrThrow(partyroomId, userId)).thenReturn(crew);

        // when
        djCommandService.dequeueDj(partyroomId);

        // then
        verify(partyroomAggregateService).removeDjFromQueue(partyroomId, new CrewId(1L));
        verify(playbackControlPort, never()).skipPlayback(any());
    }
}
