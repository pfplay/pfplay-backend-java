package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.partyroom.converter.ParyPermissionConverter;
import com.pfplaybackend.api.partyroom.enums.PartyPermissionRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("파티 권한")
@Table(
    name = "PARTY_PERMISSION",
    indexes = {
        @Index(name = "party_permission_authority", columnList = "authority")
    }
)
public class PartyPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    private PartyPermissionRole authority;

    @Comment("파티 정보 수정")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean partyInfoFetch;
    
    @Comment("파티룸 폐쇄")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean partyClose;

    @Comment("공지 작성/삭제/수정")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean notice;

    @Comment("클러버 권한 부여")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean giveToClubber;

    @Comment("채팅 삭제")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean chatDelete;

    @Comment("클러버 패널티 30초 채팅 금지")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean chatLimitBanToClubber;

    @Comment("클러버 패널티 30초 킥(재입장 가능)")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean kickToClubber;

    @Comment("클러버 패널티 30초 킥(재입장 불가능)")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean banToClubber;

    @Comment("채팅 차단")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean chatBan;

    @Comment("DJ 대기열 잠금")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean djWaitLock;

    @Comment("신규 DJ 추가/삭제")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean newDj;

    @Comment("음악 스킵")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean musicSkip;

    @Comment("영상 길이 제한")
    @Convert(converter = ParyPermissionConverter.class)
    private Boolean videoLengthLimit;

    @Builder
    public PartyPermission(PartyPermissionRole authority, Boolean partyInfoFetch, Boolean partyClose, Boolean notice, Boolean giveToClubber, Boolean chatDelete, Boolean chatLimitBanToClubber, Boolean kickToClubber, Boolean banToClubber, Boolean chatBan, Boolean djWaitLock, Boolean newDj, Boolean musicSkip, Boolean videoLengthLimit) {
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
