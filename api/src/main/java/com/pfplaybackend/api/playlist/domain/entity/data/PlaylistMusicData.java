package com.pfplaybackend.api.playlist.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.user.domain.value.UserId;
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
        name = "PLAYLIST_MUSIC",
        indexes = {
            @Index(name = "playlist_music_playlist_id_IDX", columnList = "playlist_id")
        }
)
@Entity
public class PlaylistMusicData extends BaseEntity {
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
    private String duration;

    @Comment("썸네일 이미지 url")
    private String thumbnailImage;

    protected PlaylistMusicData() { }

    @Builder
    public PlaylistMusicData(PlaylistData playlistData, Integer orderNumber, String name, String duration, String linkId, String thumbnailImage) {
        this.playlistData = playlistData;
        this.orderNumber = orderNumber;
        this.name = name;
        this.duration = duration;
        this.linkId = linkId;
        this.thumbnailImage = thumbnailImage;
    }
}
