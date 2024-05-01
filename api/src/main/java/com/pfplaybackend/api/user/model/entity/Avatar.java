package com.pfplaybackend.api.user.model.entity;

import com.pfplaybackend.api.user.model.enums.ObtainmentType;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "AVATAR")
@Entity
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ObtainmentType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String resourceUrl;

    @Column(nullable = false)
    private Integer obtainableScore;

    @Column(nullable = false)
    private Boolean isCombinable;

    public Avatar() { }
}
