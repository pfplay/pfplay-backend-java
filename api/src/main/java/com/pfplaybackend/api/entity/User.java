package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.entity.audit.BaseTime;
import com.pfplaybackend.api.common.enums.Authority;
import com.pfplaybackend.api.user.presentation.request.ProfileUpdateRequest;
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
public class User extends BaseTime {
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

    protected User() { }

    @Builder
    public User(String email, String nickname, String introduction, Authority authority, String walletAddress, Integer djScore, Integer taskScore, Integer bodyId, String faceUrl) {
        this.email = email;
        this.nickname = nickname;
        this.introduction = introduction;
        this.authority = authority;
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
}
