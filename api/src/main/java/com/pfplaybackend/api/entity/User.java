package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.enums.Authority;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class User {

    @Id
    private String email;

    private String nickname;

    private String introduction;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String walletAddress;

    private String djScore;

    private String taskScore;

    private LocalDateTime createTime;

    private String accessToken;
    private String refreshToken;

    public User() { }
}
