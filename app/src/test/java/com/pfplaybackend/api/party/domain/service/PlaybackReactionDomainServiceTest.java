package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.domain.model.ReactionPostProcessResult;
import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.enums.MotionType;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.model.ReactionState;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlaybackReactionDomainServiceTest {

    private final PlaybackReactionDomainService service = new PlaybackReactionDomainService();

    // ========== getReactionStateByHistory ==========

    @Test
    @DisplayName("getReactionStateByHistory — 히스토리 데이터에서 ReactionState를 올바르게 매핑한다")
    void getReactionStateByHistory_mapsCorrectly() {
        // given
        PlaybackReactionHistoryData history = new PlaybackReactionHistoryData(new UserId(1L), new PlaybackId(1L));
        history.applyReactionState(new ReactionState(true, false, true));

        // when
        ReactionState state = service.getReactionStateByHistory(history);

        // then
        assertThat(state.liked()).isTrue();
        assertThat(state.disliked()).isFalse();
        assertThat(state.grabbed()).isTrue();
    }

    @Test
    @DisplayName("getReactionStateByHistory — 초기 히스토리는 모든 반응이 false인 상태를 반환한다")
    void getReactionStateByHistory_initial() {
        // given
        PlaybackReactionHistoryData history = new PlaybackReactionHistoryData(new UserId(1L), new PlaybackId(1L));

        // when
        ReactionState state = service.getReactionStateByHistory(history);

        // then
        assertThat(state).isEqualTo(ReactionState.createBaseState());
    }

    // ========== getTargetReactionState ==========

    @Test
    @DisplayName("getTargetReactionState — 기본 상태에서 LIKE 반응 시 올바른 대상 상태를 반환한다")
    void getTargetReactionState_baseLike() {
        // given
        ReactionState base = ReactionState.createBaseState();

        // when
        ReactionState target = service.getTargetReactionState(base, ReactionType.LIKE);

        // then
        assertThat(target).isEqualTo(new ReactionState(true, false, false));
    }

    // ========== determinePostProcessing ==========

    @Nested
    @DisplayName("determinePostProcessing")
    class DeterminePostProcessing {

        @Test
        @DisplayName("같은 상태 전환 시 모든 변경 플래그가 false이다")
        void sameState_noChanges() {
            // given
            ReactionState liked = new ReactionState(true, false, false);

            // when
            ReactionPostProcessResult result = service.determinePostProcessing(liked, liked);

            // then
            assertThat(result.motionChanged()).isFalse();
            assertThat(result.djActivityScoreChanged()).isFalse();
            assertThat(result.aggregationChanged()).isFalse();
            assertThat(result.grabStatusChanged()).isFalse();
        }

        @Test
        @DisplayName("기본 상태에서 좋아요 전환 시 모션과 점수가 변경된다")
        void base_toLiked() {
            // given
            ReactionState base = new ReactionState(false, false, false);
            ReactionState liked = new ReactionState(true, false, false);

            // when
            ReactionPostProcessResult result = service.determinePostProcessing(base, liked);

            // then
            assertThat(result.motionChanged()).isTrue();
            assertThat(result.determinedMotionType()).isEqualTo(MotionType.DANCE_TYPE_1);
            assertThat(result.djActivityScoreChanged()).isTrue();
            assertThat(result.deltaScore()).isEqualTo(1);
            assertThat(result.aggregationChanged()).isTrue();
            assertThat(result.deltaRecord()).isEqualTo(List.of(1, 0, 0));
        }

        @Test
        @DisplayName("좋아요에서 좋아요+그랩 전환 시 그랩 상태가 변경된다")
        void liked_toLikedGrabbed() {
            // given
            ReactionState liked = new ReactionState(true, false, false);
            ReactionState likedGrabbed = new ReactionState(true, false, true);

            // when
            ReactionPostProcessResult result = service.determinePostProcessing(liked, likedGrabbed);

            // then
            assertThat(result.motionChanged()).isTrue();
            assertThat(result.determinedMotionType()).isEqualTo(MotionType.DANCE_TYPE_2);
            assertThat(result.djActivityScoreChanged()).isTrue();
            assertThat(result.deltaScore()).isEqualTo(2);
            assertThat(result.grabStatusChanged()).isTrue();
            assertThat(result.deltaRecord()).isEqualTo(List.of(0, 0, 1));
        }

        @Test
        @DisplayName("좋아요에서 싫어요 전환 시 모션이 NONE으로 변경된다")
        void liked_toDisliked() {
            // given
            ReactionState liked = new ReactionState(true, false, false);
            ReactionState disliked = new ReactionState(false, true, false);

            // when
            ReactionPostProcessResult result = service.determinePostProcessing(liked, disliked);

            // then
            assertThat(result.motionChanged()).isTrue();
            assertThat(result.determinedMotionType()).isEqualTo(MotionType.NONE);
            assertThat(result.djActivityScoreChanged()).isTrue();
            assertThat(result.deltaScore()).isEqualTo(-1);
            assertThat(result.aggregationChanged()).isTrue();
            assertThat(result.deltaRecord()).isEqualTo(List.of(-1, 1, 0));
        }

        @Test
        @DisplayName("그랩 상태가 이미 true이면 isGrabStatusChanged는 false이다")
        void grabbedToGrabbed_noGrabChange() {
            // given — 좋아요+그랩 → 싫어요+그랩 (grab stays true)
            ReactionState likedGrabbed = new ReactionState(true, false, true);
            ReactionState dislikedGrabbed = new ReactionState(false, true, true);

            // when
            ReactionPostProcessResult result = service.determinePostProcessing(likedGrabbed, dislikedGrabbed);

            // then
            assertThat(result.grabStatusChanged()).isFalse();
        }
    }
}
