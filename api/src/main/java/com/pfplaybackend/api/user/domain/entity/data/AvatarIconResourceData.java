package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.user.domain.enums.PairType;
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

    @Column(nullable = false)
    private PairType pairType;

    public AvatarIconResourceData() {}

    @Builder
    public AvatarIconResourceData(Long id, String name, String resourceUri, PairType pairType) {
        this.id = id;
        this.name = name;
        this.resourceUri = resourceUri;
        this.pairType = pairType;
    }
}