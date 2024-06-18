package com.pfplaybackend.api.playlist.domain.model.entity;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.playlist.domain.model.enums.PlaylistType;
import com.pfplaybackend.api.user.domain.model.value.UserId;
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
        name = "PLAYLIST",
        indexes = {
                @Index(name = "playlist_uid_IDX", columnList = "uid")
        }
)
@Entity
public class PlaylistData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Embedded
    private UserId userId;

    @Comment("플레이리스트 순서")
    @Column(columnDefinition = "integer unsigned")
    private Integer orderNumber;

    @Comment("플레이리스트 이름")
    private String name;

    @Comment("플레이리스트 타입")
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private PlaylistType type;

    protected PlaylistData() { }

    @Builder
    public PlaylistData(UserId userId, Integer orderNumber, String name, PlaylistType type) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.name = name;
        this.type = type;
    }


//    public void rename(String name) {
//        this.name = name;
//    }
}
