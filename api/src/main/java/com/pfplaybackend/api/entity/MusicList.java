package com.pfplaybackend.api.entity;

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
@Table(name = "MUSIC_LIST")
@Entity
public class MusicList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "play_list_id", foreignKey = @ForeignKey(name = "fk_music_list_play_list_id"))
    private PlayList playList;

    @Comment("플레이리스트의 곡 순서")
    @Column(columnDefinition = "integer unsigned")
    private Long order;

    @Comment("곡 이름")
    private String name;

    @Comment("곡 총 재생 시간")
    private String duration;

    @Comment("url")
    private String url;

    @Column(columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected MusicList() {
    }

    @Builder
    public MusicList(PlayList playList, Long order, String name, String duration, String url, LocalDateTime updatedAt) {
        this.playList = playList;
        this.order = order;
        this.name = name;
        this.duration = duration;
        this.url = url;
        this.updatedAt = updatedAt;
    }
}
