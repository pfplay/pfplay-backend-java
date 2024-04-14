package com.pfplaybackend.api.user.model.domain.profile;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.user.enums.AvatarType;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "AVATAR")
@Entity
public class Avatar extends BaseEntity {

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
    private Integer point;

    @Column(nullable = false)
    private Boolean isUniform;

    public Avatar() { }
}
