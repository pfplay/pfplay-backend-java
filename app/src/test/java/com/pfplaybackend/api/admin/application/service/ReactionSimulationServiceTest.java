package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.admin.application.port.out.AdminPartyroomPort;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.application.port.out.UserActivityPort;
import com.pfplaybackend.api.party.application.service.PlaybackCommandService;
import com.pfplaybackend.api.party.application.service.PlaybackQueryService;
import com.pfplaybackend.api.party.application.service.PlaybackReactionPostProcessCommandService;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackAggregationData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.enums.MotionType;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.model.ReactionPostProcessResult;
import com.pfplaybackend.api.party.domain.model.ReactionState;
import com.pfplaybackend.api.party.domain.service.PlaybackReactionDomainService;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionSimulationServiceTest {

    @Mock
    private AdminPartyroomPort adminPartyroomPort;

    @Mock
    private PlaybackQueryService playbackQueryService;

    @Mock
    private PlaybackCommandService playbackCommandService;

    @Mock
    private PlaybackReactionDomainService playbackReactionDomainService;

    @Mock
    private PlaybackReactionPostProcessCommandService playbackReactionPostProcessCommandService;

    @Mock
    private UserActivityPort userActivityPort;

    @InjectMocks
    private ReactionSimulationService reactionSimulationService;

    private void setupCommonMocks(PlaybackId playbackId, UserId userId, ReactionPostProcessResult postProcess) {
        // Reaction history not found -> new history created
        when(adminPartyroomPort.findReactionHistory(eq(playbackId), eq(userId)))
                .thenReturn(Optional.empty());

        // Domain service: base state -> target state
        ReactionState baseState = ReactionState.createBaseState();
        ReactionState targetState = new ReactionState(true, false, false);
        when(playbackReactionDomainService.getTargetReactionState(baseState, ReactionType.LIKE))
                .thenReturn(targetState);
        when(playbackReactionDomainService.determinePostProcessing(baseState, targetState))
                .thenReturn(postProcess);

        // Save reaction history
        when(adminPartyroomPort.saveReactionHistory(any(PlaybackReactionHistoryData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Playback data for post-processing (lenient: not all tests trigger all branches)
        PlaybackData playback = mock(PlaybackData.class);
        lenient().when(playback.getUserId()).thenReturn(new UserId(99L));
        lenient().when(playback.getId()).thenReturn(playbackId.getId());
        when(playbackQueryService.getPlaybackById(playbackId)).thenReturn(playback);
    }

    @Test
    @DisplayName("simulateReaction \u2014 \uc0c8 \ub9ac\uc561\uc158 \ud788\uc2a4\ud1a0\ub9ac \uc0dd\uc131 \uc2dc \uc800\uc7a5\uc774 \ud638\ucd9c\ub41c\ub2e4")
    void simulateReaction_newHistory_savesCalled() {
        // given
        UserId userId = new UserId(1L);
        CrewId crewId = new CrewId(10L);
        PlaybackId playbackId = new PlaybackId(100L);
        PartyroomId partyroomId = new PartyroomId(1000L);

        ReactionPostProcessResult postProcess = new ReactionPostProcessResult(
                false, true, false, false,
                List.of(0, 0, 0), 0, MotionType.DANCE_TYPE_1
        );
        setupCommonMocks(playbackId, userId, postProcess);

        // when
        reactionSimulationService.simulateReaction(userId, crewId, playbackId, partyroomId, ReactionType.LIKE, 0);

        // then
        verify(adminPartyroomPort).saveReactionHistory(any(PlaybackReactionHistoryData.class));
    }

    @Test
    @DisplayName("simulateReaction \u2014 DJ \uc810\uc218 \ubcc0\uacbd \uc2dc userActivityPort\uac00 \ud638\ucd9c\ub41c\ub2e4")
    void simulateReaction_djScoreChanged_updatesActivity() {
        // given
        UserId userId = new UserId(2L);
        CrewId crewId = new CrewId(20L);
        PlaybackId playbackId = new PlaybackId(200L);
        PartyroomId partyroomId = new PartyroomId(2000L);

        ReactionPostProcessResult postProcess = new ReactionPostProcessResult(
                false, true, true, false,
                List.of(0, 0, 0), 1, MotionType.DANCE_TYPE_1
        );
        setupCommonMocks(playbackId, userId, postProcess);

        // when
        reactionSimulationService.simulateReaction(userId, crewId, playbackId, partyroomId, ReactionType.LIKE, 0);

        // then
        verify(userActivityPort).updateDjPointScore(new UserId(99L), 1);
    }

    @Test
    @DisplayName("simulateReaction \u2014 \uc5b4\uadf8\ub9ac\uac8c\uc774\uc158 \ubcc0\uacbd \uc2dc updatePlaybackAggregation\uc774 \ud638\ucd9c\ub41c\ub2e4")
    void simulateReaction_aggregationChanged_updatesAggregation() {
        // given
        UserId userId = new UserId(3L);
        CrewId crewId = new CrewId(30L);
        PlaybackId playbackId = new PlaybackId(300L);
        PartyroomId partyroomId = new PartyroomId(3000L);

        List<Integer> deltaRecord = List.of(1, 0, 0);
        ReactionPostProcessResult postProcess = new ReactionPostProcessResult(
                true, true, false, false,
                deltaRecord, 0, MotionType.DANCE_TYPE_1
        );
        setupCommonMocks(playbackId, userId, postProcess);

        PlaybackAggregationData aggregation = mock(PlaybackAggregationData.class);
        when(playbackCommandService.updatePlaybackAggregation(new PlaybackId(playbackId.getId()), deltaRecord))
                .thenReturn(aggregation);

        // when
        reactionSimulationService.simulateReaction(userId, crewId, playbackId, partyroomId, ReactionType.LIKE, 0);

        // then
        verify(playbackCommandService).updatePlaybackAggregation(new PlaybackId(playbackId.getId()), deltaRecord);
        verify(playbackReactionPostProcessCommandService).publishAggregationChangedEvent(partyroomId, aggregation);
    }
}
