package com.pfplaybackend.api.party.domain.specification;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GradeAdjustmentSpecificationTest {

    private GradeAdjustmentSpecification spec;

    @BeforeEach
    void setUp() {
        spec = new GradeAdjustmentSpecification();
    }

    private CrewData crewWithGrade(GradeType grade) {
        return CrewData.builder().gradeType(grade).build();
    }

    @Test
    @DisplayName("MODERATOR가 LISTENER를 CLUBBER로 승급 — 성공")
    void validAdjustment() {
        CrewData adjuster = crewWithGrade(GradeType.MODERATOR);
        CrewData subject = crewWithGrade(GradeType.LISTENER);
        assertThatNoException().isThrownBy(() ->
                spec.validate(adjuster, subject, GradeType.CLUBBER, AuthorityTier.FM));
    }

    @Test
    @DisplayName("CLUBBER가 등급 조정 시도 — MANAGER_GRADE_REQUIRED")
    void adjusterBelowManagerGrade() {
        CrewData adjuster = crewWithGrade(GradeType.CLUBBER);
        CrewData subject = crewWithGrade(GradeType.LISTENER);
        assertThatThrownBy(() -> spec.validate(adjuster, subject, GradeType.CLUBBER, AuthorityTier.FM))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("게스트를 LISTENER 이외로 설정 — GUEST_ONLY_POSSIBLE_LISTENER")
    void guestOnlyListener() {
        CrewData adjuster = crewWithGrade(GradeType.MODERATOR);
        CrewData subject = crewWithGrade(GradeType.LISTENER);
        assertThatThrownBy(() -> spec.validate(adjuster, subject, GradeType.CLUBBER, AuthorityTier.GT))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("HOST 등급 설정 시도 — UNABLE_TO_SET_HOST")
    void cannotSetHost() {
        CrewData adjuster = crewWithGrade(GradeType.COMMUNITY_MANAGER);
        CrewData subject = crewWithGrade(GradeType.LISTENER);
        assertThatThrownBy(() -> spec.validate(adjuster, subject, GradeType.HOST, AuthorityTier.FM))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("조정자 등급이 대상보다 낮음 — GRADE_INSUFFICIENT")
    void adjusterLowerThanSubject() {
        CrewData adjuster = crewWithGrade(GradeType.MODERATOR);
        CrewData subject = crewWithGrade(GradeType.COMMUNITY_MANAGER);
        assertThatThrownBy(() -> spec.validate(adjuster, subject, GradeType.LISTENER, AuthorityTier.FM))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("목표 등급이 조정자 등급 이상 — GRADE_EXCEEDS_THRESHOLD")
    void targetGradeExceedsAdjuster() {
        CrewData adjuster = crewWithGrade(GradeType.MODERATOR);
        CrewData subject = crewWithGrade(GradeType.LISTENER);
        assertThatThrownBy(() -> spec.validate(adjuster, subject, GradeType.MODERATOR, AuthorityTier.FM))
                .isInstanceOf(ForbiddenException.class);
    }
}
