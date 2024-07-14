package com.pfplaybackend.api.user.domain.entity.data;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Table(name = "AVATAR_ICON_RESOURCE")
@Entity
public class AvatarIconResourceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String resourceUri;

    public AvatarIconResourceData() {}

    @Builder
    public AvatarIconResourceData(Long id, String name, String resourceUri) {
        this.id = id;
        this.name = name;
        this.resourceUri = resourceUri;
    }
}