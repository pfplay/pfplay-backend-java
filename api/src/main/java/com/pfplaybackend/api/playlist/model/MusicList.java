package com.pfplaybackend.api.playlist.model;

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
        name = "MUSIC_LIST",
        indexes = {
            @Index(name = "music_list_play_list_id_IDX", columnList = "play_list_id")
        }
)
@Entity
public class MusicList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "play_list_id")
    private PlayList playList;

    @Comment("곡 고유 id")
    private String uid;

    @Comment("플레이리스트의 곡 순서")
    @Column(columnDefinition = "integer unsigned")
    private Integer orderNumber;

    @Comment("곡 이름")
    private String name;

    @Comment("곡 총 재생 시간")
    private String duration;

    @Comment("썸네일 이미지 url")
    private String thumbnailImage;

    protected MusicList() { }

    @Builder
    public MusicList(PlayList playList, String uid, Integer orderNumber, String name, String duration, String thumbnailImage) {
        this.playList = playList;
        this.uid = uid;
        this.orderNumber = orderNumber;
        this.name = name;
        this.duration = duration;
        this.thumbnailImage = thumbnailImage;
    }
}
