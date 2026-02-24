package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackReactionHistoryRepository;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.model.ReactionPostProcessResult;
import com.pfplaybackend.api.party.domain.model.ReactionState;
import com.pfplaybackend.api.party.domain.service.PlaybackReactionDomainService;
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

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaybackReactionCommandServiceTest {

    @Mock PlaybackReactionHistoryRepository playbackReactionHistoryRepository;
    @Mock PlaybackReactionDomainService playbackReactionDomainService;
    @Mock PartyroomQueryService partyroomQueryService;
    @Mock PlaybackReactionPostProcessCommandService playbackReactionPostProcessCommandService;
    @Mock PlaybackReactionQueryService playbackReactionQueryService;

    @InjectMocks PlaybackReactionCommandService playbackReactionCommandService;

    private final UserId userId = new UserId(1L);
    private final PartyroomId partyroomId = new PartyroomId(10L);
    private final PlaybackId playbackId = new PlaybackId(100L);

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
    @DisplayName("LIKE 반응 시 정상적으로 후처리가 호출되고 결과가 반환된다")
    void reactToCurrentPlaybackLikeSuccess() {
        // given
        ActivePartyroomDto activePartyroom = new ActivePartyroomDto(
                partyroomId.getId(), false, 5L, true, playbackId, new CrewId(5L));
        when(partyroomQueryService.getMyActivePartyroom()).thenReturn(Optional.of(activePartyroom));

        PlaybackReactionHistoryData historyData = new PlaybackReactionHistoryData(userId, playbackId);
        when(playbackReactionQueryService.findPrevHistoryData(playbackId, userId))
                .thenReturn(Optional.empty());

        ReactionState baseState = ReactionState.createBaseState();
        ReactionState targetState = new ReactionState(true, false, false);
        when(playbackReactionDomainService.getTargetReactionState(baseState, ReactionType.LIKE))
                .thenReturn(targetState);

        ReactionPostProcessResult postProcessResult = new ReactionPostProcessResult(false, false, false, false, null, 0, null);
        when(playbackReactionDomainService.determinePostProcessing(baseState, targetState))
                .thenReturn(postProcessResult);
        when(playbackReactionHistoryRepository.save(any())).thenReturn(historyData);

        CrewData crew = CrewData.builder().id(5L).userId(userId).build();
        when(partyroomQueryService.getCrewByUserId(partyroomId, userId)).thenReturn(Optional.of(crew));

        // when
        Map<String, Boolean> result = playbackReactionCommandService.reactToCurrentPlayback(partyroomId, ReactionType.LIKE);

        // then
        assertThat(result).containsEntry("isLiked", true);
        assertThat(result).containsEntry("isDisliked", false);
        assertThat(result).containsEntry("isGrabbed", false);
        verify(playbackReactionPostProcessCommandService).postProcess(
                eq(postProcessResult), eq(ReactionType.LIKE), eq(partyroomId), eq(playbackId), eq(new CrewId(5L)));
    }

    @Test
    @DisplayName("게스트 사용자가 GRAB 반응 시 예외가 발생한다")
    void reactToCurrentPlaybackGuestGrabThrowsException() {
        // given
        AuthContext guestContext = mock(AuthContext.class);
        lenient().when(guestContext.getUserId()).thenReturn(userId);
        when(guestContext.getAuthorityTier()).thenReturn(AuthorityTier.GT);
        ThreadLocalContext.setContext(guestContext);

        // when & then
        assertThatThrownBy(() -> playbackReactionCommandService.reactToCurrentPlayback(partyroomId, ReactionType.GRAB))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("기존 반응 이력이 있으면 해당 이력을 기반으로 상태를 결정한다")
    void reactToCurrentPlaybackWithExistingHistory() {
        // given
        ActivePartyroomDto activePartyroom = new ActivePartyroomDto(
                partyroomId.getId(), false, 5L, true, playbackId, new CrewId(5L));
        when(partyroomQueryService.getMyActivePartyroom()).thenReturn(Optional.of(activePartyroom));

        PlaybackReactionHistoryData historyData = mock(PlaybackReactionHistoryData.class);
        when(historyData.getId()).thenReturn(99L);
        when(historyData.applyReactionState(any())).thenReturn(historyData);
        when(playbackReactionQueryService.findPrevHistoryData(playbackId, userId))
                .thenReturn(Optional.of(historyData));

        ReactionState existingState = new ReactionState(true, false, false);
        when(playbackReactionDomainService.getReactionStateByHistory(historyData))
                .thenReturn(existingState);

        ReactionState targetState = new ReactionState(false, false, false);
        when(playbackReactionDomainService.getTargetReactionState(existingState, ReactionType.LIKE))
                .thenReturn(targetState);

        ReactionPostProcessResult postProcessResult = new ReactionPostProcessResult(false, false, false, false, null, 0, null);
        when(playbackReactionDomainService.determinePostProcessing(existingState, targetState))
                .thenReturn(postProcessResult);
        when(playbackReactionHistoryRepository.save(any())).thenReturn(historyData);

        CrewData crew = CrewData.builder().id(5L).userId(userId).build();
        when(partyroomQueryService.getCrewByUserId(partyroomId, userId)).thenReturn(Optional.of(crew));

        // when
        Map<String, Boolean> result = playbackReactionCommandService.reactToCurrentPlayback(partyroomId, ReactionType.LIKE);

        // then
        assertThat(result).containsEntry("isLiked", false);
        verify(playbackReactionDomainService).getReactionStateByHistory(historyData);
    }
}
