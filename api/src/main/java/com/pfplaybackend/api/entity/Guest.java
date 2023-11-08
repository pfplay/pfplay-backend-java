package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.entity.audit.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@DynamicUpdate
@DynamicInsert
@Table(name = "GUEST")
@Entity
public class Guest extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint unsigned")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private String agent;

    @Column(nullable = false, columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;

    public Guest() { }

    @Builder
    public Guest(String name, String agent) {
        this.name = name;
        this.agent = agent;
    }
}
