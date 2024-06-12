package com.pfplaybackend.api.user.domain.model.domain;

import com.pfplaybackend.api.user.domain.model.data.ActivityData;
import com.pfplaybackend.api.user.domain.model.enums.ActivityType;
import com.pfplaybackend.api.user.domain.model.value.UserId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Activity {

    private Long id;
    private final UserId userId;
    private final ActivityType activityType;
    private final Integer score;

    public Activity(UserId userId, ActivityType activityType, Integer score) {
        this.id = null;
        this.userId = userId;
        this.activityType = activityType;
        this.score = score;
    }

    @Builder
    public Activity(Long id, UserId userId, ActivityType activityType, Integer score) {
        this.id = id;
        this.userId = userId;
        this.activityType = activityType;
        this.score = score;
    }

    ActivityData toData() {
        return ActivityData.builder()
                .id(this.id)
                .userId(this.userId)
                .activityType(this.activityType)
                .score(this.score)
                .build();
    }
}
