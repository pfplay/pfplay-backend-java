package com.pfplaybackend.api.user.model.entity;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.user.model.domain.ActivityDomain;
import com.pfplaybackend.api.user.model.enums.ActivityType;
import com.pfplaybackend.api.user.model.value.UserId;
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
public class Activity extends BaseEntity {

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

    protected Activity() {}

    @Builder
    public Activity(Long id, UserId userId, ActivityType activityType, Integer score) {
        this.id = id;
        this.userId = userId;
        this.activityType = activityType;
        this.score = score;
    }

    ActivityDomain toDomain() {
        return null;
    }
}
