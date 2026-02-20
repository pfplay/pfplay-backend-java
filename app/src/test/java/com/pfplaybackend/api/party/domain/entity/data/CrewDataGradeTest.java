package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.party.domain.enums.GradeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CrewDataGradeTest {

    private CrewData crewWithGrade(GradeType grade) {
        return CrewData.builder().gradeType(grade).build();
    }

    @Test
    @DisplayName("isBelowGrade — LISTENER는 MODERATOR보다 낮음")
    void isBelowGrade_true() {
        CrewData crew = crewWithGrade(GradeType.LISTENER);
        assertThat(crew.isBelowGrade(GradeType.MODERATOR)).isTrue();
    }

    @Test
    @DisplayName("isBelowGrade — MODERATOR는 MODERATOR보다 낮지 않음")
    void isBelowGrade_false_equal() {
        CrewData crew = crewWithGrade(GradeType.MODERATOR);
        assertThat(crew.isBelowGrade(GradeType.MODERATOR)).isFalse();
    }

    @Test
    @DisplayName("isBelowGrade — HOST는 MODERATOR보다 낮지 않음")
    void isBelowGrade_false_higher() {
        CrewData crew = crewWithGrade(GradeType.HOST);
        assertThat(crew.isBelowGrade(GradeType.MODERATOR)).isFalse();
    }

    @Test
    @DisplayName("isGradeHigherThan — HOST > LISTENER")
    void isGradeHigherThan_true() {
        CrewData host = crewWithGrade(GradeType.HOST);
        CrewData listener = crewWithGrade(GradeType.LISTENER);
        assertThat(host.isGradeHigherThan(listener)).isTrue();
    }

    @Test
    @DisplayName("isGradeHigherThan — LISTENER vs MODERATOR")
    void isGradeHigherThan_false() {
        CrewData listener = crewWithGrade(GradeType.LISTENER);
        CrewData moderator = crewWithGrade(GradeType.MODERATOR);
        assertThat(listener.isGradeHigherThan(moderator)).isFalse();
    }

    @Test
    @DisplayName("isGradeHigherThan — 같은 등급")
    void isGradeHigherThan_equal() {
        CrewData mod1 = crewWithGrade(GradeType.MODERATOR);
        CrewData mod2 = crewWithGrade(GradeType.MODERATOR);
        assertThat(mod1.isGradeHigherThan(mod2)).isFalse();
    }
}
