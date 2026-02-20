package com.pfplaybackend.api.user.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreTest {

    @Test
    @DisplayName("Score 생성 및 값 조회")
    void create() {
        Score score = new Score(100);
        assertThat(score.getValue()).isEqualTo(100);
    }

    @Test
    @DisplayName("음수 값은 0으로 클램핑")
    void negativeClampedToZero() {
        Score score = new Score(-5);
        assertThat(score.getValue()).isEqualTo(0);
    }

    @Test
    @DisplayName("add는 새로운 Score를 반환 (불변)")
    void addReturnsNewScore() {
        Score original = new Score(10);
        Score added = original.add(5);

        assertThat(added.getValue()).isEqualTo(15);
        assertThat(original.getValue()).isEqualTo(10);
    }

    @Test
    @DisplayName("add에서 음수 결과는 0으로 클램핑")
    void addNegativeClampedToZero() {
        Score score = new Score(3);
        Score result = score.add(-10);
        assertThat(result.getValue()).isEqualTo(0);
    }

    @Test
    @DisplayName("isAtLeast 임계값 비교")
    void isAtLeast() {
        Score score = new Score(50);
        assertThat(score.isAtLeast(50)).isTrue();
        assertThat(score.isAtLeast(49)).isTrue();
        assertThat(score.isAtLeast(51)).isFalse();
    }

    @Test
    @DisplayName("zero 팩토리 메서드")
    void zero() {
        Score score = Score.zero();
        assertThat(score.getValue()).isEqualTo(0);
    }

    @Test
    @DisplayName("equals와 hashCode 동작")
    void equalsAndHashCode() {
        Score s1 = new Score(10);
        Score s2 = new Score(10);
        Score s3 = new Score(20);

        assertThat(s1).isEqualTo(s2);
        assertThat(s1).isNotEqualTo(s3);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
    }
}
