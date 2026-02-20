package com.pfplaybackend.api.party.domain.model;

import com.pfplaybackend.api.party.domain.enums.MotionType;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReactionStateResolverTest {

    // ========== resolve ==========

    @Nested
    @DisplayName("resolve")
    class Resolve {

        @Test
        @DisplayName("기본 상태(F,F,F) → NONE, score=0")
        void resolve_base() {
            ResolvedReaction result = ReactionStateResolver.resolve(new ReactionState(false, false, false));
            assertThat(result.getMotionType()).isEqualTo(MotionType.NONE);
            assertThat(result.getScore()).isZero();
        }

        @Test
        @DisplayName("좋아요 상태(T,F,F) → DANCE_TYPE_1, score=1")
        void resolve_liked() {
            ResolvedReaction result = ReactionStateResolver.resolve(new ReactionState(true, false, false));
            assertThat(result.getMotionType()).isEqualTo(MotionType.DANCE_TYPE_1);
            assertThat(result.getScore()).isEqualTo(1);
        }

        @Test
        @DisplayName("좋아요+그랩 상태(T,F,T) → DANCE_TYPE_2, score=3")
        void resolve_likedAndGrabbed() {
            ResolvedReaction result = ReactionStateResolver.resolve(new ReactionState(true, false, true));
            assertThat(result.getMotionType()).isEqualTo(MotionType.DANCE_TYPE_2);
            assertThat(result.getScore()).isEqualTo(3);
        }

        @Test
        @DisplayName("싫어요 상태(F,T,F) → NONE, score=0")
        void resolve_disliked() {
            ResolvedReaction result = ReactionStateResolver.resolve(new ReactionState(false, true, false));
            assertThat(result.getMotionType()).isEqualTo(MotionType.NONE);
            assertThat(result.getScore()).isZero();
        }

        @Test
        @DisplayName("싫어요+그랩 상태(F,T,T) → NONE, score=2")
        void resolve_dislikedAndGrabbed() {
            ResolvedReaction result = ReactionStateResolver.resolve(new ReactionState(false, true, true));
            assertThat(result.getMotionType()).isEqualTo(MotionType.NONE);
            assertThat(result.getScore()).isEqualTo(2);
        }
    }

    // ========== getCombinedReactionState ==========

    @Nested
    @DisplayName("getCombinedReactionState")
    class GetCombinedReactionState {

        @Test
        @DisplayName("기본 상태에서 LIKE → (T,F,F)")
        void base_like() {
            ReactionState result = ReactionStateResolver.getCombinedReactionState(
                    new ReactionState(false, false, false), ReactionType.LIKE);
            assertThat(result).isEqualTo(new ReactionState(true, false, false));
        }

        @Test
        @DisplayName("기본 상태에서 DISLIKE → (F,T,F)")
        void base_dislike() {
            ReactionState result = ReactionStateResolver.getCombinedReactionState(
                    new ReactionState(false, false, false), ReactionType.DISLIKE);
            assertThat(result).isEqualTo(new ReactionState(false, true, false));
        }

        @Test
        @DisplayName("기본 상태에서 GRAB → (T,F,T)")
        void base_grab() {
            ReactionState result = ReactionStateResolver.getCombinedReactionState(
                    new ReactionState(false, false, false), ReactionType.GRAB);
            assertThat(result).isEqualTo(new ReactionState(true, false, true));
        }

        @Test
        @DisplayName("좋아요 상태에서 DISLIKE → (F,T,F)")
        void liked_dislike() {
            ReactionState result = ReactionStateResolver.getCombinedReactionState(
                    new ReactionState(true, false, false), ReactionType.DISLIKE);
            assertThat(result).isEqualTo(new ReactionState(false, true, false));
        }

        @Test
        @DisplayName("싫어요 상태에서 GRAB → (T,F,T)")
        void disliked_grab() {
            ReactionState result = ReactionStateResolver.getCombinedReactionState(
                    new ReactionState(false, true, false), ReactionType.GRAB);
            assertThat(result).isEqualTo(new ReactionState(true, false, true));
        }
    }
}
