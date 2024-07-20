package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Table(name = "AVATAR_FACE_RESOURCE")
@Entity
public class AvatarFaceResourceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String resourceUri;

    public AvatarFaceResourceData() {}

    @Builder
    public AvatarFaceResourceData(Long id, String name, String resourceUri) {
        this.id = id;
        this.name = name;
        this.resourceUri = resourceUri;
    }
}
