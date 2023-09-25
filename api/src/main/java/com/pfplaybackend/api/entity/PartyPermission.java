package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.partyroom.converter.PartyPermissionConverter;
import com.pfplaybackend.api.partyroom.enums.PartyPermissionRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("파티 권한")
@Table(
    name = "PARTY_PERMISSION",
    indexes = {
        @Index(name = "idx_permission_authority", columnList = "authority")
    }
)
public class PartyPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    private PartyPermissionRole authority;

    @Comment("파티 정보 수정")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean partyInfoFetch;
    
    @Comment("파티룸 폐쇄")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean partyClose;

    @Comment("공지 작성/삭제/수정")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean notice;

    @Comment("클러버 권한 부여")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean giveToClubber;

    @Comment("채팅 삭제")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean chatDelete;

    @Comment("클러버 패널티 30초 채팅 금지")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean chatLimitBanToClubber;

    @Comment("클러버 패널티 30초 킥(재입장 가능)")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean kickToClubber;

    @Comment("클러버 패널티 30초 킥(재입장 불가능)")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean banToClubber;

    @Comment("채팅 차단")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean chatBan;

    @Comment("DJ 대기열 잠금")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean djWaitLock;

    @Comment("신규 DJ 추가/삭제")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean newDj;

    @Comment("음악 스킵")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean musicSkip;

    @Comment("영상 길이 제한")
    @Convert(converter = PartyPermissionConverter.class)
    private Boolean videoLengthLimit;

    @Builder
    public PartyPermission(PartyPermissionRole authority, Boolean partyInfoFetch, Boolean partyClose,
                           Boolean notice, Boolean giveToClubber, Boolean chatDelete,
                           Boolean chatLimitBanToClubber, Boolean kickToClubber,
                           Boolean banToClubber, Boolean chatBan, Boolean djWaitLock,
                           Boolean newDj, Boolean musicSkip, Boolean videoLengthLimit) {
        this.authority = authority;
        this.partyInfoFetch = partyInfoFetch;
        this.partyClose = partyClose;
        this.notice = notice;
        this.giveToClubber = giveToClubber;
        this.chatDelete = chatDelete;
        this.chatLimitBanToClubber = chatLimitBanToClubber;
        this.kickToClubber = kickToClubber;
        this.banToClubber = banToClubber;
        this.chatBan = chatBan;
        this.djWaitLock = djWaitLock;
        this.newDj = newDj;
        this.musicSkip = musicSkip;
        this.videoLengthLimit = videoLengthLimit;
    }

}
