package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.user.conveter.UserPermissionConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Comment("기능 접근 권한")
@Table(name = "USER_PERMISSION",
        indexes = @Index(
            name = "user_permission_authority",
            columnList = "authority"))
public class UserPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    @Comment("프로필 설정")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean settingProfile;

    @Comment("파티목록 화면")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean showPartyListDisplay;

    @Comment("메인 스테이지 입장")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean enterMainStage;

    @Comment("채팅")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean chat;

    @Comment("플레이리스트 생성")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean createPlayList;

    @Comment("DJ 대기열 등록")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean createWaitDj;

    @Comment("파티룸 입장")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean enterPartyRoom;

    @Comment("파티룸 생성")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean createPartyRoom;

    @Comment("계급 권한 관리자")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean admin;

    @Comment("계급 권한")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean communityManager;

    @Comment("계급 권한")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean moderator;

    @Comment("계급 권한")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean clubber;

    @Comment("계급 권한")
    @Convert(converter = UserPermissionConverter.class)
    private Boolean listener;

    public UserPermission() { }


    @Builder
    public UserPermission(Authority authority, Boolean settingProfile, Boolean showPartyListDisplay, Boolean enterMainStage, Boolean chat, Boolean createPlayList, Boolean createWaitDj, Boolean enterPartyRoom, Boolean createPartyRoom, Boolean admin, Boolean communityManager, Boolean moderator, Boolean clubber, Boolean listener) {
        this.authority = authority;
        this.settingProfile = settingProfile;
        this.showPartyListDisplay = showPartyListDisplay;
        this.enterMainStage = enterMainStage;
        this.chat = chat;
        this.createPlayList = createPlayList;
        this.createWaitDj = createWaitDj;
        this.enterPartyRoom = enterPartyRoom;
        this.createPartyRoom = createPartyRoom;
        this.admin = admin;
        this.communityManager = communityManager;
        this.moderator = moderator;
        this.clubber = clubber;
        this.listener = listener;
    }
}
