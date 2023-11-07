package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.avatar.enums.AvatarType;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "AVATAR")
@Entity
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AvatarType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private Long point;

    @Column(nullable = false)
    private Boolean isUniform;

    public Avatar() { }
}
