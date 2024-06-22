package com.pfplaybackend.api.playlist.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
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
        name = "PLAYLIST",
        indexes = {
                @Index(name = "playlist_owner_id_IDX", columnList = "owner_id")
        }
)
@Entity
public class PlaylistData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "owner_id")),
    })
    private UserId ownerId;

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
    public PlaylistData(UserId ownerId, Integer orderNumber, String name, PlaylistType type) {
        this.ownerId = ownerId;
        this.orderNumber = orderNumber;
        this.name = name;
        this.type = type;
    }


//    public void rename(String name) {
//        this.name = name;
//    }
}
