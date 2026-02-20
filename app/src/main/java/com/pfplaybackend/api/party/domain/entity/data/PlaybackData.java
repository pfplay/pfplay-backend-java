package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.common.domain.value.Duration;
import com.pfplaybackend.api.common.domain.value.DurationConverter;
import com.pfplaybackend.api.playlist.application.dto.PlaybackTrackDto;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
            @AttributeOverride(name = "id", column = @Column(name = "partyroom_id")),
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
    @Convert(converter = DurationConverter.class)
    private Duration duration;
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
                        UserId userId, String name, String linkId, Duration duration, String thumbnailImage, int grabCount, int likeCount, int dislikeCount, Long endTime) {
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

    // ── Factory Method ──

    public static PlaybackData create(PartyroomId partyroomId, UserId userId, PlaybackTrackDto trackDto) {
        Duration dur = Duration.fromString(trackDto.duration());
        return PlaybackData.builder()
                .partyroomId(partyroomId)
                .userId(userId)
                .name(trackDto.name())
                .duration(dur)
                .linkId(trackDto.linkId())
                .thumbnailImage(trackDto.thumbnailImage())
                .grabCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .endTime(dur.calculateEndTimeEpochMilli())
                .build();
    }

    // ── Business Methods ──

    public PlaybackData updateAggregation(int deltaLikeCount, int deltaDislikeCount, int deltaGrabCount) {
        this.likeCount += deltaLikeCount;
        this.grabCount += deltaGrabCount;
        this.dislikeCount += deltaDislikeCount;
        return this;
    }
}
