package com.pfplaybackend.api.party.domain.entity.data.history;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.model.ReactionState;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaybackReactionHistoryDataTest {

    @Test
    @DisplayName("생성자 — 초기 상태는 모두 false다")
    void constructorAllReactionsFalseByDefault() {
        // when
        PlaybackReactionHistoryData history = new PlaybackReactionHistoryData(
                new UserId(1L), new PlaybackId(100L)
        );

        // then
        assertThat(history.isLiked()).isFalse();
        assertThat(history.isDisliked()).isFalse();
        assertThat(history.isGrabbed()).isFalse();
    }

    @Test
    @DisplayName("applyReactionState — ReactionState가 올바르게 적용된다")
    void applyReactionStateAppliesCorrectly() {
        // given
        PlaybackReactionHistoryData history = new PlaybackReactionHistoryData(
                new UserId(1L), new PlaybackId(100L)
        );
        ReactionState likeState = ReactionState.createState(ReactionType.LIKE);

        // when
        history.applyReactionState(likeState);

        // then
        assertThat(history.isLiked()).isTrue();
        assertThat(history.isDisliked()).isFalse();
        assertThat(history.isGrabbed()).isFalse();
    }

    @Test
    @DisplayName("applyReactionState — this를 반환한다")
    void applyReactionStateReturnsThis() {
        // given
        PlaybackReactionHistoryData history = new PlaybackReactionHistoryData(
                new UserId(1L), new PlaybackId(100L)
        );
        ReactionState grabState = ReactionState.createState(ReactionType.GRAB);

        // when
        PlaybackReactionHistoryData result = history.applyReactionState(grabState);

        // then
        assertThat(result).isSameAs(history);
        assertThat(result.isLiked()).isTrue();
        assertThat(result.isGrabbed()).isTrue();
    }
}
