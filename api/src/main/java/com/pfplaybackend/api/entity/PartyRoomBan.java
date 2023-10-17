package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.entity.audit.BaseTime;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.partyroom.converter.PartyRoomBanConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Comment("밴 유저 목록")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "PARTY_ROOM_BAN",
        indexes = {
                @Index(name = "idx_party_room_ban_01", columnList = "user_id, party_room_id, authority"),
        }
)
public class PartyRoomBan extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint unsigned")
    private Long id;

    @Column(columnDefinition = "bigint unsigned", name = "user_id")
    // role에 따라 guest pk가 들어갈 수 있음
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_room_id")
    private PartyRoom partyRoom;

    @Convert(converter = PartyRoomBanConverter.class)
    @ColumnDefault("0")
    private Boolean ban;

    @Convert(converter = PartyRoomBanConverter.class)
    @ColumnDefault("0")
    private Boolean kick;

    @Comment("30초 채팅금지")
    @ColumnDefault("0")
    @Convert(converter = PartyRoomBanConverter.class)
    private Boolean chat;

    @Comment("밴,킥 사유")
    private String reason;

    @Comment("유저 롤 타입")
    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    @Builder
    public PartyRoomBan(Long userId, PartyRoom partyRoom, Boolean ban,
                        Boolean kick, Boolean chat, String reason,
                        Authority authority) {
        this.userId = userId;
        this.partyRoom = partyRoom;
        this.ban = ban;
        this.kick = kick;
        this.chat = chat;
        this.reason = reason;
        this.authority = authority;
    }
}
