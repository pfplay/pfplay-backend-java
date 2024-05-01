package com.pfplaybackend.api.user.model.domain;

import com.pfplaybackend.api.user.model.entity.Activity;
import com.pfplaybackend.api.user.model.enums.ActivityType;
import com.pfplaybackend.api.user.model.value.UserId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActivityDomain {
    private Long id;
    private final UserId userId;
    private final ActivityType activityType;
    private final Integer score;

    public ActivityDomain(UserId userId, ActivityType activityType, Integer score) {
        this.id = null;
        this.userId = userId;
        this.activityType = activityType;
        this.score = score;
    }

    Activity toEntity() {
        return Activity.builder()
                .id(this.id)
                .userId(this.userId)
                .activityType(this.activityType)
                .score(this.score)
                .build();
    }
}
