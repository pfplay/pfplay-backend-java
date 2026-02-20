package com.pfplaybackend.api.party.domain.entity.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaybackAggregationDataTest {

    @Test
    @DisplayName("createFor — 팩토리 메서드로 생성 시 모든 카운트가 0으로 초기화된다")
    void createFor_defaultZero() {
        // when
        PlaybackAggregationData aggregation = PlaybackAggregationData.createFor(1L);

        // then
        assertThat(aggregation.getPlaybackId()).isEqualTo(1L);
        assertThat(aggregation.getLikeCount()).isZero();
        assertThat(aggregation.getDislikeCount()).isZero();
        assertThat(aggregation.getGrabCount()).isZero();
    }

    @Test
    @DisplayName("updateAggregation — 양수 델타로 카운트가 증가한다")
    void updateAggregation_positiveDelta() {
        // given
        PlaybackAggregationData aggregation = PlaybackAggregationData.createFor(1L);

        // when
        aggregation.updateAggregation(3, 1, 2);

        // then
        assertThat(aggregation.getLikeCount()).isEqualTo(3);
        assertThat(aggregation.getDislikeCount()).isEqualTo(1);
        assertThat(aggregation.getGrabCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("updateAggregation — 음수 델타로 카운트가 감소한다")
    void updateAggregation_negativeDelta() {
        // given
        PlaybackAggregationData aggregation = PlaybackAggregationData.createFor(1L);
        aggregation.updateAggregation(5, 3, 2);

        // when
        aggregation.updateAggregation(-2, -1, 0);

        // then
        assertThat(aggregation.getLikeCount()).isEqualTo(3);
        assertThat(aggregation.getDislikeCount()).isEqualTo(2);
        assertThat(aggregation.getGrabCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("updateAggregation — 누적 호출 시 카운트가 합산된다")
    void updateAggregation_accumulates() {
        // given
        PlaybackAggregationData aggregation = PlaybackAggregationData.createFor(1L);

        // when
        aggregation.updateAggregation(1, 0, 0);
        aggregation.updateAggregation(1, 1, 0);
        aggregation.updateAggregation(0, 0, 1);

        // then
        assertThat(aggregation.getLikeCount()).isEqualTo(2);
        assertThat(aggregation.getDislikeCount()).isEqualTo(1);
        assertThat(aggregation.getGrabCount()).isEqualTo(1);
    }
}
