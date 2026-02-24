package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Table(name = "AVATAR_BODY_RESOURCE")
@Entity
public class AvatarBodyResourceData {
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

    private int combinePositionX;
    private int combinePositionY;

    public AvatarBodyResourceData() {
    }

    @Builder
    public AvatarBodyResourceData(Long id, String name, String resourceUri,
                                  ObtainmentType obtainableType, int obtainableScore,
                                  boolean isCombinable, boolean isDefaultSetting, int combinePositionX, int combinePositionY) {
        this.id = id;
        this.name = name;
        this.resourceUri = resourceUri;
        this.obtainableType = obtainableType;
        this.obtainableScore = obtainableScore;
        this.isCombinable = isCombinable;
        this.isDefaultSetting = isDefaultSetting;
        this.combinePositionX = combinePositionX;
        this.combinePositionY = combinePositionY;
    }

    public void updateResource(String resourceUri, ObtainmentType obtainableType, int obtainableScore,
                               boolean isCombinable, boolean isDefaultSetting, int combinePositionX, int combinePositionY) {
        this.resourceUri = resourceUri;
        this.obtainableType = obtainableType;
        this.obtainableScore = obtainableScore;
        this.isCombinable = isCombinable;
        this.isDefaultSetting = isDefaultSetting;
        this.combinePositionX = combinePositionX;
        this.combinePositionY = combinePositionY;
    }

}