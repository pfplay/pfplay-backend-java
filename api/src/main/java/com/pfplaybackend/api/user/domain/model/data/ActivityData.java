package com.pfplaybackend.api.user.domain.model.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.user.domain.model.domain.Activity;
import com.pfplaybackend.api.user.domain.model.enums.ActivityType;
import com.pfplaybackend.api.user.domain.model.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(name = "USER_ACTIVITY",
        indexes = {
                @Index(name = "user_activity_uid_IDX", columnList = "uid")
        })
@Entity
public class ActivityData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Embedded
    private UserId userId;

    @Column(length = 50)
    @Enumerated(value = EnumType.STRING)
    private ActivityType activityType;

    @Column(columnDefinition = "integer unsigned")
    private Integer score;

    protected ActivityData() {}

    @Builder
    public ActivityData(Long id, UserId userId, ActivityType activityType, Integer score) {
        this.id = id;
        this.userId = userId;
        this.activityType = activityType;
        this.score = score;
    }

    Activity toDomain() {
        return Activity.builder()
                .id(this.id)
                .userId(this.userId)
                .activityType(this.activityType)
                .score(this.score)
                .build();
    }
}
