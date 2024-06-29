package com.pfplaybackend.api.user.domain.entity.domainmodel;

import com.pfplaybackend.api.user.domain.entity.data.AvatarResourceData;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AvatarResource {

    private Long id;
    private final String name;
    private final String resourceUri;
    private final ObtainmentType obtainableType;
    private final int obtainableScore;
    private final boolean isCombinable;
    private final boolean isDefaultSetting;
    // Face 와 결합을 위한 좌표 정보
    private final int x;
    private final int y;


    AvatarResource(String name, String resourceUri, ObtainmentType obtainableType,
                   int obtainableScore, boolean isCombinable, boolean isDefaultSetting, int x, int y) {
        this.name = name;
        this.resourceUri = resourceUri;
        this.obtainableType = obtainableType;
        this.obtainableScore = obtainableScore;
        this.isCombinable = isCombinable;
        this.isDefaultSetting = isDefaultSetting;
        this.x = x;
        this.y = y;
    }

    @Builder
    AvatarResource(Long id, String name, String resourceUri, ObtainmentType obtainableType,
                   int obtainableScore, boolean isCombinable, boolean isDefaultSetting, int x, int y) {
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

    static public AvatarResource create(String name, String resourceUri, ObtainmentType obtainableType,
                                        int obtainableScore, boolean isCombinable, boolean isDefaultSetting, int x, int y) {
        return new AvatarResource(name, resourceUri, obtainableType, obtainableScore, isCombinable, isDefaultSetting, x, y);
    }

    public AvatarResourceData toData() {
        return AvatarResourceData.builder()
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