package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.Score;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityDataTest {

    @Test
    @DisplayName("create — 팩토리 메서드가 올바르게 생성한다")
    void createSetsAllFields() {
        // given
        UserId userId = new UserId(1L);

        // when
        ActivityData activity = ActivityData.create(userId, ActivityType.DJ_PNT, 100);

        // then
        assertThat(activity.getUserId()).isEqualTo(userId);
        assertThat(activity.getActivityType()).isEqualTo(ActivityType.DJ_PNT);
        assertThat(activity.getScore()).isEqualTo(new Score(100));
    }

    @Test
    @DisplayName("addScore — 점수가 누적된다")
    void addScoreAccumulatesScore() {
        // given
        ActivityData activity = ActivityData.create(new UserId(1L), ActivityType.DJ_PNT, 50);

        // when
        activity.addScore(30);

        // then
        assertThat(activity.getScore()).isEqualTo(new Score(80));
    }
}
