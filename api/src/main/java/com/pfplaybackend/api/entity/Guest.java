package com.pfplaybackend.api.entity;

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
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, columnDefinition = "varchar(2)  default 0")
    private String kick;

    @Column(nullable = false, columnDefinition = "varchar(2) default 0")
    private String ban;

    private String agent;

    private String reason;

    @Column(nullable = false, columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Guest() { }

    @Builder
    public Guest(String name, String kick, String ban, String agent, String reason, LocalDateTime updatedAt) {
        this.name = name;
        this.kick = kick;
        this.ban = ban;
        this.agent = agent;
        this.reason = reason;
        this.updatedAt = updatedAt;
    }
}
