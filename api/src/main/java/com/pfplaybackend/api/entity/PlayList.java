package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.playlist.enums.PlayListType;
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
@Table(name = "PLAY_LIST",
        indexes = {
                @Index(name = "idx_play_list", columnList = "user_id")
        })
@Entity
public class PlayList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Comment("플레이리스트 순서")
    @Column(columnDefinition = "integer unsigned")
    private Long orderNumber;

    @Comment("플레이리스트 이름")
    private String name;

    @Comment("플레이리스트 타입")
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private PlayListType type;

    @Column(columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected PlayList() {
    }

    @Builder
    public PlayList(User user, Long orderNumber, String name, PlayListType type, LocalDateTime updatedAt) {
        this.user = user;
        this.orderNumber = orderNumber;
        this.name = name;
        this.type = type;
        this.updatedAt = updatedAt;
    }
}
