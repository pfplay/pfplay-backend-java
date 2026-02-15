package com.pfplaybackend.api.profile.domain;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.profile.domain.enums.FaceSourceType;
import com.pfplaybackend.api.profile.domain.vo.Avatar;
import com.pfplaybackend.api.profile.domain.vo.AvatarBody;
import com.pfplaybackend.api.profile.domain.vo.AvatarFace;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Profile;
import com.pfplaybackend.api.user.domain.value.*;
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
                @Index(name = "user_profile_user_id_IDX", columnList = "user_id")
        })
@Entity
public class ProfileData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "user_id")),
    })
    private UserId userId;

    @Column(length = 20)
    private String nickname;

    @Column(length = 50)
    private String introduction;

    @Embedded
    private WalletAddress walletAddress;

    @Embedded
    private AvatarBodyUri avatarBodyUri;

    @Embedded
    private AvatarFaceUri avatarFaceUri;

    @Embedded
    private AvatarIconUri avatarIconUri;

    private AvatarCompositionType avatarCompositionType;
    private FaceSourceType faceSourceType;
    private int combinePositionX;
    private int combinePositionY;
    private double offsetX;
    private double offsetY;
    private double scale;

    protected ProfileData() {}

    @Builder
    public ProfileData(Long id, UserId userId, String nickname, String introduction,
                       AvatarBodyUri avatarBodyUri,
                       AvatarFaceUri avatarFaceUri,
                       AvatarIconUri avatarIconUri,
                       WalletAddress walletAddress,
                       AvatarCompositionType avatarCompositionType,
                       FaceSourceType faceSourceType,
                       int combinePositionX, int combinePositionY,
                       double offsetX, double offsetY, double scale) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.introduction = introduction;
        this.avatarBodyUri = avatarBodyUri;
        this.avatarFaceUri = avatarFaceUri;
        this.avatarIconUri = avatarIconUri;
        this.walletAddress = walletAddress;
        this.avatarCompositionType = avatarCompositionType;
        this.faceSourceType = faceSourceType;
        this.combinePositionX = combinePositionX;
        this.combinePositionY = combinePositionY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
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
        if (this.avatarIconUri == null) {
            this.avatarIconUri = new AvatarIconUri("");
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
                .avatarIconUri(this.avatarIconUri)
                .walletAddress(this.walletAddress)
                .avatarCompositionType(this.avatarCompositionType)
                .faceSourceType(this.faceSourceType)
                .combinePositionX(this.combinePositionX)
                .combinePositionY(this.combinePositionY)
                .offsetX(this.offsetX)
                .offsetY(this.offsetY)
                .scale(this.scale)
                .build();
    }

    public void updateAvatar(Avatar avatar) {
        AvatarBody body = avatar.body();
        AvatarFace face = avatar.face();
        this.avatarBodyUri = new AvatarBodyUri(body.uri());
    }
}