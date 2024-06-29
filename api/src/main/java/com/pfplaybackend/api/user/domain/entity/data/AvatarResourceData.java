package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarResource;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Table(name = "AVATAR_RESOURCE")
@Entity
public class AvatarResourceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String resourceUri;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ObtainmentType obtainableType;

    @Column(nullable = false)
    private int obtainableScore;

    @Column(nullable = false)
    private boolean isCombinable;

    @Column(nullable = false)
    private boolean isDefaultSetting;

    private int x;
    private int y;


    public AvatarResourceData() {}

    @Builder
    public AvatarResourceData(Long id, String name, String resourceUri,
                              ObtainmentType obtainableType, int obtainableScore,
                              boolean isCombinable, boolean isDefaultSetting, int x, int y) {
        this.id = id;
        this.name = name;
        this.resourceUri = resourceUri;
        this.obtainableType = obtainableType;
        this.obtainableScore = obtainableScore;
        this.isCombinable = isCombinable;
        this.isDefaultSetting = isDefaultSetting;
        this.x = x;
        this.y = y;
    }

    public AvatarResource toDomain() {
        return AvatarResource.builder()
                .id(this.id)
                .name(this.name)
                .resourceUri(this.resourceUri)
                .obtainableType(this.obtainableType)
                .obtainableScore(this.obtainableScore)
                .isCombinable(this.isCombinable)
                .isDefaultSetting(this.isDefaultSetting)
                .x(this.x)
                .y(this.y)
                .build();
    }
}