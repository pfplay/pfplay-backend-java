package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackReactionHistoryRepository;
import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaybackReactionQueryServiceTest {

    @Mock PlaybackReactionHistoryRepository playbackReactionHistoryRepository;
    @InjectMocks PlaybackReactionQueryService playbackReactionQueryService;

    @Test
    @DisplayName("findPrevHistoryData — 이력이 있으면 Optional에 감싸서 반환한다")
    void findPrevHistoryDataReturnsHistoryWhenExists() {
        // given
        PlaybackId playbackId = new PlaybackId(1L);
        UserId userId = new UserId(10L);
        PlaybackReactionHistoryData historyData = new PlaybackReactionHistoryData(userId, playbackId);

        when(playbackReactionHistoryRepository.findByPlaybackIdAndUserId(playbackId, userId))
                .thenReturn(Optional.of(historyData));

        // when
        Optional<PlaybackReactionHistoryData> result = playbackReactionQueryService.findPrevHistoryData(playbackId, userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("findPrevHistoryData — 이력이 없으면 Optional.empty를 반환한다")
    void findPrevHistoryDataReturnsEmptyWhenNotExists() {
        // given
        PlaybackId playbackId = new PlaybackId(1L);
        UserId userId = new UserId(10L);

        when(playbackReactionHistoryRepository.findByPlaybackIdAndUserId(playbackId, userId))
                .thenReturn(Optional.empty());

        // when
        Optional<PlaybackReactionHistoryData> result = playbackReactionQueryService.findPrevHistoryData(playbackId, userId);

        // then
        assertThat(result).isEmpty();
    }
}
