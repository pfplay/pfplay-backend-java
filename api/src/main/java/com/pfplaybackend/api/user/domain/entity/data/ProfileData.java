package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
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

    @Column(length = 30)
    private String introduction;

    @Embedded
    private WalletAddress walletAddress;

    @Embedded
    private AvatarBodyUri avatarBodyUri;

    @Embedded
    private AvatarFaceUri avatarFaceUri;

    @Embedded
    private AvatarIconUri avatarIconUri;

    private int combinePositionX;
    private int combinePositionY;

    protected ProfileData() {}

    @Builder
    public ProfileData(Long id, UserId userId, String nickname, String introduction,
                       AvatarBodyUri avatarBodyUri,
                       AvatarFaceUri avatarFaceUri,
                       AvatarIconUri avatarIconUri,
                       WalletAddress walletAddress,
                       int combinePositionX, int combinePositionY) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.introduction = introduction;
        this.avatarBodyUri = avatarBodyUri;
        this.avatarFaceUri = avatarFaceUri;
        this.avatarIconUri = avatarIconUri;
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
                .combinePositionX(this.combinePositionX)
                .combinePositionY(this.combinePositionY)
                .build();
    }
}
