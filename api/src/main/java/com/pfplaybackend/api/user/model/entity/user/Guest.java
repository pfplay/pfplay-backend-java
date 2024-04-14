package com.pfplaybackend.api.user.model.entity.user;

import com.pfplaybackend.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicUpdate
@DynamicInsert
@Table(name = "GUEST")
@Entity
public class Guest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint unsigned")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private String agent;

    public Guest() { }

    @Builder
    public Guest(String name, String agent) {
        this.name = name;
        this.agent = agent;
    }
}
