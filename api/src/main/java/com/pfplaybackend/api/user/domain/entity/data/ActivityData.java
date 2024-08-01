package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.UserId;
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
                @Index(name = "user_activity_user_id_IDX", columnList = "user_id")
        })
@Entity
public class ActivityData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "user_id")),
    })
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
