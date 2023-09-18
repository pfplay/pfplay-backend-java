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
@Table( name = "USER",
        uniqueConstraints = {
            @UniqueConstraint(name = "unique_user_email", columnNames = {"email"})
        })
@Entity
public class User {

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

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;

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
    public User(String email, Authority authority) {
        this.email = email;
        this.authority = authority;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setBodyId(Integer bodyId) {
        this.bodyId = bodyId;
    }
}
