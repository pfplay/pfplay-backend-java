package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.entity.audit.BaseTime;
import com.pfplaybackend.api.partyroom.enums.PartyPermissionRole;
import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Comment("파티룸 접속 테이블")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table( name = "PARTY_ROOM_JOIN",
        indexes = {
                @Index(name = "idx_party_room_join_01", columnList = "room_id, user_id"),
        })
public class PartyRoomJoin extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint unsigned")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private PartyRoom partyRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_id")
    private PartyRoomBan partyRoomBan;

    @Comment("파티 권한")
    @Enumerated(EnumType.STRING)
    private PartyPermissionRole role;

    @Comment(value = "파티 활성화 여부")
    @Enumerated(EnumType.STRING)
    private PartyRoomStatus active;

    @Builder
    public PartyRoomJoin(PartyRoom partyRoom, User user,
                         PartyRoomBan partyRoomBan, PartyPermissionRole role,
                         PartyRoomStatus active) {
        this.partyRoom = partyRoom;
        this.user = user;
        this.partyRoomBan = partyRoomBan;
        this.role = role;
        this.active = active;
    }

}
