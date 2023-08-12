package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.enums.Authority;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Getter
@DynamicInsert
@Table(name = "USER")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer UNSIGNED")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String nickname;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private String introduction;
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String walletAddress;
    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    private String faceUrl;
    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    private Integer bodyId;
    public void setBodyId(Integer bodyId) {
        this.bodyId = bodyId;
    }

    @Column(columnDefinition = "integer default 0")
    private Integer djScore;

    @Column(columnDefinition = "integer default 0")
    private Integer taskScore;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;

    protected User() { }

    @Builder
    public User(String email, Authority authority) {
        this.email = email;
        this.authority = authority;
    }
}
