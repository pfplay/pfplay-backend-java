package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.entity.audit.BaseTime;
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
@Table(
        name = "PLAY_LIST",
        indexes = {
                @Index(name = "play_list_user_id_IDX", columnList = "user_id")
        }
)
@Entity
public class PlayList extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Comment("플레이리스트 순서")
    @Column(columnDefinition = "integer unsigned")
    private Integer orderNumber;

    @Comment("플레이리스트 이름")
    private String name;

    @Comment("플레이리스트 타입")
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private PlayListType type;

    protected PlayList() { }

    @Builder
    public PlayList(User user, Integer orderNumber, String name, PlayListType type) {
        this.user = user;
        this.orderNumber = orderNumber;
        this.name = name;
        this.type = type;
    }
}
