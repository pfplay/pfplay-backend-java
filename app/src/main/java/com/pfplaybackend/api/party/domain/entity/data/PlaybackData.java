package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.common.domain.value.Duration;
import com.pfplaybackend.api.common.domain.value.DurationConverter;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.domain.value.UserId;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(
        name = "PLAYBACK",
        indexes = {
                @Index(name = "playback_partyroom_id_IDX", columnList = "partyroom_id")
        }
)
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

    // 재생 종료 시각
    private Long endTime;

    protected PlaybackData() {}

    @Builder
    public PlaybackData(Long id, PartyroomId partyroomId,
                        UserId userId, String name, String linkId, Duration duration, String thumbnailImage, Long endTime) {
        this.id = id;
        this.partyroomId = partyroomId;
        this.userId = userId;
        this.name = name;
        this.linkId = linkId;
        this.duration = duration;
        this.thumbnailImage = thumbnailImage;
        this.endTime = endTime;
    }

    // ── Factory Method ──

    public static PlaybackData create(PartyroomId partyroomId, UserId userId,
                                       String name, String duration, String linkId, String thumbnailImage) {
        Duration dur = Duration.fromString(duration);
        return PlaybackData.builder()
                .partyroomId(partyroomId)
                .userId(userId)
                .name(name)
                .duration(dur)
                .linkId(linkId)
                .thumbnailImage(thumbnailImage)
                .endTime(dur.calculateEndTimeEpochMilli())
                .build();
    }
}
