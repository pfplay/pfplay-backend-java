package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Profile;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.WalletAddress;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(name = "USER_PROFILE",
        indexes = {
                @Index(name = "user_profile_uid_IDX", columnList = "uid")
        })
@Entity
public class ProfileData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Embedded
    private UserId userId;

    @Column(length = 20)
    private String nickname;

    @Column(length = 30)
    private String introduction;

    @Embedded
    private WalletAddress walletAddress;

    @Embedded
    private AvatarFaceUri avatarFaceUri;

    @Embedded
    private AvatarBodyUri avatarBodyUri;

    private int combinePositionX;
    private int combinePositionY;

    protected ProfileData() {}

    @Builder
    public ProfileData(Long id, UserId userId, String nickname, String introduction, AvatarFaceUri avatarFaceUri,
                       AvatarBodyUri avatarBodyUri, WalletAddress walletAddress,
                       int combinePositionX, int combinePositionY) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.introduction = introduction;
        this.avatarBodyUri = avatarBodyUri;
        this.avatarFaceUri = avatarFaceUri;
        this.walletAddress = walletAddress;
        this.combinePositionX = combinePositionX;
        this.combinePositionY = combinePositionY;
    }

    @PrePersist
    @PreUpdate
    private void setDefaultValues() {
        if (this.avatarBodyUri == null) {
            this.avatarBodyUri = new AvatarBodyUri("");
        }
        if (this.avatarFaceUri == null) {
            this.avatarFaceUri = new AvatarFaceUri("");
        }
        if (this.walletAddress == null) {
            this.walletAddress = new WalletAddress("");
        }
    }

    public Profile toDomain() {
        return Profile.builder()
                .id(this.id)
                .userId(this.userId)
                .nickname(this.nickname)
                .introduction(this.introduction)
                .avatarBodyUri(this.avatarBodyUri)
                .avatarFaceUrl(this.avatarFaceUri)
                .walletAddress(this.walletAddress)
                .combinePositionX(this.combinePositionX)
                .combinePositionY(this.combinePositionY)
                .build();
    }
}
