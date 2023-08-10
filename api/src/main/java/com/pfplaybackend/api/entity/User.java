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

    private String introduction;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String walletAddress;

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
