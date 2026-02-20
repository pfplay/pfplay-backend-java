package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(name = "PLAYBACK_AGGREGATION")
@Entity
public class PlaybackAggregationData extends BaseEntity {

    @Id
    @Column(name = "playback_id")
    private Long playbackId;

    private int likeCount;
    private int dislikeCount;
    private int grabCount;

    public PlaybackAggregationData() {}

    private PlaybackAggregationData(Long playbackId) {
        this.playbackId = playbackId;
        this.likeCount = 0;
        this.dislikeCount = 0;
        this.grabCount = 0;
    }

    // ── Factory Method ──

    public static PlaybackAggregationData createFor(Long playbackId) {
        return new PlaybackAggregationData(playbackId);
    }

    // ── Business Methods ──

    public PlaybackAggregationData updateAggregation(int deltaLikeCount, int deltaDislikeCount, int deltaGrabCount) {
        this.likeCount += deltaLikeCount;
        this.dislikeCount += deltaDislikeCount;
        this.grabCount += deltaGrabCount;
        return this;
    }
}
