package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PartyroomPlaybackDataTest {

    @Test
    @DisplayName("createFor — 생성 시 비활성 상태로 초기화된다")
    void createFor_defaultState() {
        // when
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(1L);

        // then
        assertThat(playbackState.getPartyroomId()).isEqualTo(1L);
        assertThat(playbackState.isActivated()).isFalse();
        assertThat(playbackState.getCurrentPlaybackId()).isNull();
        assertThat(playbackState.getCurrentDjCrewId()).isNull();
    }

    @Test
    @DisplayName("activate — 활성화 시 playbackId와 crewId가 설정되고 isActivated가 true가 된다")
    void activate() {
        // given
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(1L);
        PlaybackId playbackId = new PlaybackId(10L);
        CrewId crewId = new CrewId(5L);

        // when
        playbackState.activate(playbackId, crewId);

        // then
        assertThat(playbackState.isActivated()).isTrue();
        assertThat(playbackState.getCurrentPlaybackId()).isEqualTo(playbackId);
        assertThat(playbackState.getCurrentDjCrewId()).isEqualTo(crewId);
    }

    @Test
    @DisplayName("deactivate — 비활성화 시 모든 상태가 초기화된다")
    void deactivate() {
        // given
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(1L);
        playbackState.activate(new PlaybackId(10L), new CrewId(5L));

        // when
        playbackState.deactivate();

        // then
        assertThat(playbackState.isActivated()).isFalse();
        assertThat(playbackState.getCurrentPlaybackId()).isNull();
        assertThat(playbackState.getCurrentDjCrewId()).isNull();
    }

    @Test
    @DisplayName("updatePlayback — playbackId와 crewId가 갱신된다")
    void updatePlayback() {
        // given
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(1L);
        playbackState.activate(new PlaybackId(10L), new CrewId(5L));

        PlaybackId newPlaybackId = new PlaybackId(20L);
        CrewId newCrewId = new CrewId(8L);

        // when
        playbackState.updatePlayback(newPlaybackId, newCrewId);

        // then
        assertThat(playbackState.getCurrentPlaybackId()).isEqualTo(newPlaybackId);
        assertThat(playbackState.getCurrentDjCrewId()).isEqualTo(newCrewId);
    }

    @Test
    @DisplayName("isCurrentDj — 현재 DJ crewId와 일치하면 true를 반환한다")
    void isCurrentDj_matching() {
        // given
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(1L);
        CrewId crewId = new CrewId(5L);
        playbackState.activate(new PlaybackId(10L), crewId);

        // when & then
        assertThat(playbackState.isCurrentDj(new CrewId(5L))).isTrue();
    }

    @Test
    @DisplayName("isCurrentDj — 현재 DJ crewId와 불일치하면 false를 반환한다")
    void isCurrentDj_notMatching() {
        // given
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(1L);
        playbackState.activate(new PlaybackId(10L), new CrewId(5L));

        // when & then
        assertThat(playbackState.isCurrentDj(new CrewId(99L))).isFalse();
    }

    @Test
    @DisplayName("isCurrentDj — currentDjCrewId가 null이면 false를 반환한다")
    void isCurrentDj_nullCrewId() {
        // given
        PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(1L);

        // when & then
        assertThat(playbackState.isCurrentDj(new CrewId(5L))).isFalse();
    }
}
