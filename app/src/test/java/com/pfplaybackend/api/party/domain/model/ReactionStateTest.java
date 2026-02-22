package com.pfplaybackend.api.party.domain.model;

import com.pfplaybackend.api.party.domain.enums.ReactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReactionStateTest {

    @Test
    @DisplayName("createBaseState — 기본 상태는 모든 반응이 false이다")
    void createBaseState() {
        // when
        ReactionState state = ReactionState.createBaseState();

        // then
        assertThat(state.liked()).isFalse();
        assertThat(state.disliked()).isFalse();
        assertThat(state.grabbed()).isFalse();
    }

    @Test
    @DisplayName("createState(LIKE) — 좋아요 상태 생성 시 liked만 true이다")
    void createState_like() {
        // when
        ReactionState state = ReactionState.createState(ReactionType.LIKE);

        // then
        assertThat(state.liked()).isTrue();
        assertThat(state.disliked()).isFalse();
        assertThat(state.grabbed()).isFalse();
    }

    @Test
    @DisplayName("createState(DISLIKE) — 싫어요 상태 생성 시 disliked만 true이다")
    void createState_dislike() {
        // when
        ReactionState state = ReactionState.createState(ReactionType.DISLIKE);

        // then
        assertThat(state.liked()).isFalse();
        assertThat(state.disliked()).isTrue();
        assertThat(state.grabbed()).isFalse();
    }

    @Test
    @DisplayName("createState(GRAB) — 그랩 상태 생성 시 liked과 grabbed이 true이다")
    void createState_grab() {
        // when
        ReactionState state = ReactionState.createState(ReactionType.GRAB);

        // then
        assertThat(state.liked()).isTrue();
        assertThat(state.disliked()).isFalse();
        assertThat(state.grabbed()).isTrue();
    }

    @Test
    @DisplayName("equals — 동일한 필드 값을 가진 두 ReactionState는 동일하다")
    void equals_sameFields() {
        // given
        ReactionState state1 = new ReactionState(true, false, true);
        ReactionState state2 = new ReactionState(true, false, true);

        // then
        assertThat(state1).isEqualTo(state2);
        assertThat(state1.hashCode()).isEqualTo(state2.hashCode());
    }

    @Test
    @DisplayName("equals — 다른 필드 값을 가진 두 ReactionState는 동일하지 않다")
    void equals_differentFields() {
        // given
        ReactionState state1 = new ReactionState(true, false, false);
        ReactionState state2 = new ReactionState(false, true, false);

        // then
        assertThat(state1).isNotEqualTo(state2);
    }
}
