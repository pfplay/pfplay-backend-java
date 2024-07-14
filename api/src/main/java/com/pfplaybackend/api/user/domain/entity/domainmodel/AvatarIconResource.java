package com.pfplaybackend.api.user.domain.entity.domainmodel;


import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarIconResourceData;

public class AvatarIconResource {

    private Long id;
    private final String name;
    private final String resourceUri;

    public AvatarIconResource(String name, String resourceUri) {
        this.name = name;
        this.resourceUri = resourceUri;
    }

    static public AvatarIconResource create(String name, String resourceUri) {
        return new AvatarIconResource(name, resourceUri);
    }

    public AvatarIconResourceData toData() {
        return AvatarIconResourceData.builder()
                .id(this.id)
                .name(this.name)
                .resourceUri(this.resourceUri)
                .build();
    }
}