package com.pfplaybackend.api.user.model.entity.activity;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.user.enums.PointType;
import com.pfplaybackend.api.user.model.entity.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(name = "USER_ACTIVITY",
        indexes = {
                @Index(name = "idx_user_activity_user_id", columnList = "user_id")
        })
@Entity
public class UserActivity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Comment("포인트 타입")
    @Column(length = 50)
    @Enumerated(value = EnumType.STRING)
    private PointType type;

    @Comment("포인트 점수")
    @Column(columnDefinition = "integer unsigned")
    private Integer point;
    @Comment("포인트 설명")
    @Column(length = 100)
    private String description;

    @Column(columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "datetime default current_timestamp")
    private LocalDateTime updatedAt;

    protected UserActivity() {
    }

    @Builder
    public UserActivity(User user, PointType type, Integer point, String description, LocalDateTime updatedAt) {
        this.user = user;
        this.type = type;
        this.point = point;
        this.description = description;
    }
}
