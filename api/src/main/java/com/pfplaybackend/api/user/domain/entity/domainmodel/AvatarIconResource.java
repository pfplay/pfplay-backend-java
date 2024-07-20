package com.pfplaybackend.api.user.domain.entity.domainmodel;


import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarIconResourceData;
import com.pfplaybackend.api.user.domain.enums.PairType;

public class AvatarIconResource {

    private Long id;
    private final String name;
    private final String resourceUri;
    private final PairType pairType;

    public AvatarIconResource(String name, String resourceUri, PairType pairType) {
        this.name = name;
        this.resourceUri = resourceUri;
        this.pairType = pairType;
    }

    static public AvatarIconResource create(String name, String resourceUri, PairType pairType) {
        return new AvatarIconResource(name, resourceUri, pairType);
    }

    public AvatarIconResourceData toData() {
        return AvatarIconResourceData.builder()
                .id(this.id)
                .name(this.name)
                .resourceUri(this.resourceUri)
                .pairType(this.pairType)
                .build();
    }
}