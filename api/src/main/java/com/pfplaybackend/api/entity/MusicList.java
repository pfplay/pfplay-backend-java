package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.entity.audit.BaseTime;
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
@Table(
        name = "MUSIC_LIST",
        indexes = {
            @Index(name = "idx_music_list_id", columnList = "play_list_id")
        }
)
@Entity
public class MusicList extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "play_list_id")
    private PlayList playList;

    @Comment("플레이리스트의 곡 순서")
    @Column(columnDefinition = "integer unsigned")
    private Long orderNumber;

    @Comment("곡 이름")
    private String name;

    @Comment("곡 총 재생 시간")
    private String duration;

    @Comment("url")
    private String url;

    protected MusicList() { }

    @Builder
    public MusicList(PlayList playList, Long orderNumber, String name, String duration, String url) {
        this.playList = playList;
        this.orderNumber = orderNumber;
        this.name = name;
        this.duration = duration;
        this.url = url;
    }
}
