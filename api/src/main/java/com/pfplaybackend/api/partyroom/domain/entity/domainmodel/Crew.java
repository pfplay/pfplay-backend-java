package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Crew {
    private long id;
    private PartyroomId partyroomId;
    private UserId userId;
    private AuthorityTier authorityTier;
    private GradeType gradeType;
    private boolean isActive;
    private boolean isBanned;
    private LocalDateTime enteredAt;
    private LocalDateTime exitedAt;

    public Crew() {}

    @Builder
    public Crew(long id, PartyroomId partyroomId, UserId userId, AuthorityTier authorityTier,
                GradeType gradeType, boolean isActive, boolean isBanned, LocalDateTime enteredAt, LocalDateTime exitedAt) {
        this.id = id;
        this.partyroomId = partyroomId;
        this.userId = userId;
        this.authorityTier = authorityTier;
        this.gradeType = gradeType;
        this.isActive = isActive;
        this.isBanned = isBanned;
        this.enteredAt = enteredAt;
        this.exitedAt = exitedAt;
    }

    public static Crew create(UserId userId, PartyroomId partyroomId, AuthorityTier authorityTier, GradeType gradeType) {
        return Crew.builder()
                .userId(userId)
                .authorityTier(authorityTier)
                .gradeType(gradeType)
                .isActive(true)
                .isBanned(false)
                .partyroomId(partyroomId)
                .enteredAt(LocalDateTime.now())
                .build();
    }

    public Crew assignPartyroomId(PartyroomId partyroomId) {
        this.partyroomId = partyroomId;
        return this;
    }

    public Crew applyDeactivation() {
        this.isActive = false;
        return this;
    }

    public Crew applyActivation() {
        this.isActive = true;
        this.enteredAt = LocalDateTime.now();
        return this;
    }

    public Crew updateGrade(GradeType gradeType) {
        this.gradeType = gradeType;
        return this;
    }
}