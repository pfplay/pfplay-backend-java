package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Instant;
import java.time.LocalTime;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(name = "PLAYBACK")
@Entity
public class PlaybackData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "partroom_id")),
    })
    private PartyroomId partyroomId;

    // 프로필 정보를 역추적 하기 위한 '사용자 식별자'
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "user_id")),
    })
    private UserId userId;

    // 음악 이름 정보
    private String name;
    // 링크 식별자
    private String linkId;
    // 음악 재생 시간
    private String duration;
    // 썸네일
    private String thumbnailImage;

    // 그랩 횟수
    private int grabCount;
    // 좋아요 횟수
    private int likeCount;
    // 싫어요 횟수
    private int dislikeCount;

    // 재생 종료 시각
    private Long endTime;

    public PlaybackData() {}

    @Builder
    public PlaybackData(Long id, PartyroomId partyroomId,
                        UserId userId, String name, String linkId, String duration, String thumbnailImage, int grabCount, int likeCount, int dislikeCount, Long endTime) {
        this.id = id;
        this.partyroomId = partyroomId;
        this.userId = userId;
        this.name = name;
        this.linkId = linkId;
        this.duration = duration;
        this.thumbnailImage = thumbnailImage;
        this.grabCount = grabCount;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.endTime = endTime;
    }
 }
