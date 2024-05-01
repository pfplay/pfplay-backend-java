package com.pfplaybackend.api.user.model.entity;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.user.model.value.AvatarBodyId;
import com.pfplaybackend.api.user.model.value.AvatarFaceUri;
import com.pfplaybackend.api.user.model.value.WalletAddress;
import com.pfplaybackend.api.user.model.value.UserId;
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
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Embedded
    private UserId userId;

    @Column(length = 10)
    private String nickname;

    @Column(length = 30)
    private String introduction;

    @Embedded
    private WalletAddress walletAddress;

    @Embedded
    private AvatarFaceUri avatarFaceUri;

    @Embedded
    private AvatarBodyId avatarBodyId;

    protected Profile() {}

    @Builder
    public Profile(UserId userId, AvatarFaceUri avatarFaceUri, AvatarBodyId avatarBodyId, WalletAddress walletAddress) {
        this.userId = userId;
        this.avatarFaceUri = avatarFaceUri;
        this.avatarBodyId = avatarBodyId;
        this.walletAddress = walletAddress;
    }
}
