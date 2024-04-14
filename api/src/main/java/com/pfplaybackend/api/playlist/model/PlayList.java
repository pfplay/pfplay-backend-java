package com.pfplaybackend.api.playlist.model;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.playlist.enums.PlayListType;
import com.pfplaybackend.api.user.model.entity.user.User;
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
        name = "PLAY_LIST",
        indexes = {
                @Index(name = "play_list_user_id_IDX", columnList = "user_id")
        }
)
@Entity
public class PlayList extends BaseEntity {

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

    public void rename(String name) {
        this.name = name;
    }
}
