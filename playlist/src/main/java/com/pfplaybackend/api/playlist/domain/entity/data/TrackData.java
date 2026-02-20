package com.pfplaybackend.api.playlist.domain.entity.data;

import com.pfplaybackend.api.common.domain.value.Duration;
import com.pfplaybackend.api.common.domain.value.DurationConverter;
import com.pfplaybackend.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


@Getter
@DynamicInsert
@DynamicUpdate
@Table(
        name = "TRACK",
        indexes = {
            @Index(name = "track_playlist_id_IDX", columnList = "playlist_id")
        }
)
@Entity
public class TrackData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    private PlaylistData playlistData;

    @Comment("플레이리스트의 곡 순서")
    @Column(columnDefinition = "integer unsigned")
    private Integer orderNumber;

    @Comment("곡 이름")
    private String name;

    @Comment("링크 식별자")
    private String linkId;

    @Comment("곡 총 재생 시간")
    @Convert(converter = DurationConverter.class)
    private Duration duration;

    @Comment("썸네일 이미지 url")
    private String thumbnailImage;

    protected TrackData() { }

    @Builder
    public TrackData(PlaylistData playlistData, Integer orderNumber, String name, Duration duration, String linkId, String thumbnailImage) {
        this.playlistData = playlistData;
        this.orderNumber = orderNumber;
        this.name = name;
        this.duration = duration;
        this.linkId = linkId;
        this.thumbnailImage = thumbnailImage;
    }

    // ── Business Methods ──

    public void reorder(int newOrderNumber) {
        this.orderNumber = newOrderNumber;
    }

    public void moveToPlaylist(PlaylistData targetPlaylist, int newOrderNumber) {
        this.playlistData = targetPlaylist;
        this.orderNumber = newOrderNumber;
    }
}
