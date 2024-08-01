package com.pfplaybackend.api.user.domain.entity.domainmodel;


import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import lombok.Builder;

public class AvatarFaceResource {

    private Long id;
    private final String name;
    private final String resourceUri;

    public AvatarFaceResource(String name, String resourceUri) {
        this.name = name;
        this.resourceUri = resourceUri;
    }

    @Builder
    public AvatarFaceResource(Long id, String name, String resourceUri) {
        this.id = id;
        this.name = name;
        this.resourceUri = resourceUri;
    }

    static public AvatarFaceResource create(String name, String resourceUri) {
        return new AvatarFaceResource(name, resourceUri);
    }

    public AvatarFaceResourceData toData() {
        return AvatarFaceResourceData.builder()
                .id(this.id)
                .name(this.name)
                .resourceUri(this.resourceUri)
                .build();
    }
}
