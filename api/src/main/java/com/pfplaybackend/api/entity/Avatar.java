package com.pfplaybackend.api.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "AVATAR")
@Entity
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", updatable = false)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "point", nullable = false)
    private Integer point;

    public Avatar() { }
}
