package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.pointhistory.enums.PointType;
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
@Table(name = "POINT_HISTORY",
        indexes = {
                @Index(name = "idx_point_history_user_id", columnList = "user_id")
        })
@Entity
public class PointHistory {

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
    private Long point;
    @Comment("포인트 설명")
    @Column(length = 100)
    private String description;

    @Column(columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "datetime default current_timestamp")
    private LocalDateTime updatedAt;

    protected PointHistory() {
    }

    @Builder
    public PointHistory(User user, PointType type, Long point, String description, LocalDateTime updatedAt) {
        this.user = user;
        this.type = type;
        this.point = point;
        this.description = description;
    }
}