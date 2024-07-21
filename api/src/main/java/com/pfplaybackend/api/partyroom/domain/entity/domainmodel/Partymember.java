package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Partymember {
    private long id;
    private PartyroomId partyroomId;
    private UserId userId;
    private AuthorityTier authorityTier;
    private GradeType gradeType;
    private boolean isActive;
    private boolean isBanned;

    public Partymember() {}

    @Builder
    public Partymember(long id, PartyroomId partyroomId, UserId userId, AuthorityTier authorityTier,
                       GradeType gradeType, boolean isActive, boolean isBanned) {
        this.id = id;
        this.partyroomId = partyroomId;
        this.userId = userId;
        this.authorityTier = authorityTier;
        this.gradeType = gradeType;
        this.isActive = isActive;
        this.isBanned = isBanned;
    }

    public static Partymember create(UserId userId, PartyroomId partyroomId, AuthorityTier authorityTier, GradeType gradeType) {
        return Partymember.builder()
                .userId(userId)
                .authorityTier(authorityTier)
                .gradeType(gradeType)
                .isActive(true)
                .isBanned(false)
                .partyroomId(partyroomId)
                .build();
    }

    public Partymember assignPartyroomId(PartyroomId partyroomId) {
        this.partyroomId = partyroomId;
        return this;
    }

    public Partymember applyDeactivation() {
        this.isActive = false;
        return this;
    }
}