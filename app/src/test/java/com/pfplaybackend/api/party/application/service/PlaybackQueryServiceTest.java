package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackHistoryDto;
import com.pfplaybackend.api.party.application.port.out.PartyroomQueryPort;
import com.pfplaybackend.api.party.application.port.out.PlaylistCommandPort;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackAggregationData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackAggregationRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaybackQueryServiceTest {

    @Mock PartyroomQueryPort partyroomQueryPort;
    @Mock PlaybackRepository playbackRepository;
    @Mock PlaybackAggregationRepository playbackAggregationRepository;
    @Mock PlaylistCommandPort playlistCommandPort;
    @Mock UserProfileQueryPort userProfileQueryPort;
    @InjectMocks PlaybackQueryService playbackQueryService;

    @Test
    @DisplayName("getPlaybackById — 재생 데이터를 ID로 조회한다")
    void getPlaybackById_success() {
        // given
        PlaybackId playbackId = new PlaybackId(1L);
        PlaybackData playback = mock(PlaybackData.class);
        when(playbackRepository.findById(1L)).thenReturn(Optional.of(playback));

        // when
        PlaybackData result = playbackQueryService.getPlaybackById(playbackId);

        // then
        assertThat(result).isSameAs(playback);
    }

    @Test
    @DisplayName("getPlaybackById — 존재하지 않으면 예외가 발생한다")
    void getPlaybackById_notFound() {
        // given
        PlaybackId playbackId = new PlaybackId(999L);
        when(playbackRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> playbackQueryService.getPlaybackById(playbackId))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }

    @Test
    @DisplayName("updatePlaybackAggregation — 집계 데이터를 업데이트하고 저장한다")
    void updatePlaybackAggregation_success() {
        // given
        Long playbackId = 1L;
        PlaybackAggregationData aggregation = mock(PlaybackAggregationData.class);
        when(playbackAggregationRepository.findById(playbackId)).thenReturn(Optional.of(aggregation));
        when(playbackAggregationRepository.save(aggregation)).thenReturn(aggregation);

        List<Integer> deltaRecord = List.of(1, -1, 0);

        // when
        PlaybackAggregationData result = playbackQueryService.updatePlaybackAggregation(playbackId, deltaRecord);

        // then
        verify(aggregation).updateAggregation(1, -1, 0);
        verify(playbackAggregationRepository).save(aggregation);
        assertThat(result).isSameAs(aggregation);
    }

    @Test
    @DisplayName("getRecentPlaybackHistory — 재생 이력이 없으면 빈 리스트를 반환한다")
    void getRecentPlaybackHistory_empty() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        when(partyroomQueryPort.getRecentPlaybackHistory(partyroomId)).thenReturn(Collections.emptyList());

        // when
        List<PlaybackHistoryDto> result = playbackQueryService.getRecentPlaybackHistory(partyroomId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getRecentPlaybackHistory — 재생 이력이 있으면 프로필 정보와 함께 반환한다")
    void getRecentPlaybackHistory_withHistory() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        UserId userId1 = new UserId(1L);
        UserId userId2 = new UserId(2L);

        PlaybackData pb1 = mock(PlaybackData.class);
        when(pb1.getUserId()).thenReturn(userId1);
        when(pb1.getName()).thenReturn("Song A");

        PlaybackData pb2 = mock(PlaybackData.class);
        when(pb2.getUserId()).thenReturn(userId2);
        when(pb2.getName()).thenReturn("Song B");

        when(partyroomQueryPort.getRecentPlaybackHistory(partyroomId)).thenReturn(List.of(pb1, pb2));

        ProfileSettingDto profile1 = new ProfileSettingDto("DJ1", null, null, null, "icon1", 0, 0, 0, 0, 0);
        ProfileSettingDto profile2 = new ProfileSettingDto("DJ2", null, null, null, "icon2", 0, 0, 0, 0, 0);
        when(userProfileQueryPort.getUsersProfileSetting(List.of(userId1, userId2)))
                .thenReturn(Map.of(userId1, profile1, userId2, profile2));

        // when
        List<PlaybackHistoryDto> result = playbackQueryService.getRecentPlaybackHistory(partyroomId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).trackName()).isEqualTo("Song A");
        assertThat(result.get(0).nickname()).isEqualTo("DJ1");
    }
}
