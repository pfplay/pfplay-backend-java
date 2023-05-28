package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.enums.Authority;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    public User(String email) {
        this.email = email;
    }
}
