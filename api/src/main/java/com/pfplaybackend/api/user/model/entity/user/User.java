package com.pfplaybackend.api.user.model.entity.user;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.common.enums.Authority;
import com.pfplaybackend.api.config.oauth2.enums.ProviderType;
import com.pfplaybackend.api.user.enums.UserTier;
import com.pfplaybackend.api.user.presentation.user.request.ProfileUpdateRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

@Getter
@DynamicInsert
@Table(
        name = "USER",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_email", columnNames = {"email"})
        }
)
@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint unsigned")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(length = 100)
    private String nickname;

    private String introduction;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String walletAddress;

    @Column(columnDefinition = "integer default 0")
    private Integer djScore;

    @Column(columnDefinition = "integer default 0")
    private Integer taskScore;

    @Column(columnDefinition = "integer unsigned default 1")
    private Integer bodyId;

    @Column(length = 500)
    private String faceUrl;

    private String refreshToken;

    private String name;
    private UserTier userTier;
    private ProviderType providerType;

    public User() { }

    @Builder
    public User(String email, String name, String nickname, String introduction, Authority authority, String walletAddress, Integer djScore, Integer taskScore, Integer bodyId, String faceUrl, UserTier userTier, ProviderType providerType) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.introduction = introduction;
        this.authority = authority;
        this.userTier = userTier;
        this.providerType = providerType;

        // TODO 분리 필요
        this.walletAddress = walletAddress;
        this.djScore = djScore;
        this.taskScore = taskScore;
        this.bodyId = bodyId;
        this.faceUrl = faceUrl;
    }

    public void setProfile(ProfileUpdateRequest dto) {
        this.introduction = dto.getIntroduction();
        this.nickname = dto.getNickname();
        this.faceUrl = dto.getFaceUrl();
        this.bodyId = dto.getBodyId();
        this.walletAddress = dto.getWalletAddress();
    }

    public void updateRefreshToken(String reIssuedRefreshToken) {
        this.refreshToken = reIssuedRefreshToken;
    }

}