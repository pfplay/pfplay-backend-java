package com.pfplaybackend.api.entity;

import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.enums.PartyRoomType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@DynamicInsert
@DynamicUpdate
@Table( name = "PARTY_ROOM",
        uniqueConstraints = {
            @UniqueConstraint(name = "unique_party_room_name", columnNames = {"name"}),
            @UniqueConstraint(name = "unique_party_room_domain", columnNames = {"domain"})
        })
@Entity
public class PartyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_party_room_user_id"))
    private User user;

    private String introduce;

    private String domain;

    @Comment("디제잉 시간")
    private Integer djingLimit;

    @Comment("파티룸 타입")
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private PartyRoomType type;

    @Column(columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Comment("파티룸 활성화 여부")
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private PartyRoomStatus status;

    protected PartyRoom() { }

    @Builder
    public PartyRoom(String name, User user, String introduce,
                     String domain, Integer djingLimit, PartyRoomType type,
                     LocalDateTime updatedAt, PartyRoomStatus status) {
        this.name = name;
        this.user = user;
        this.introduce = introduce;
        this.domain = domain;
        this.djingLimit = djingLimit;
        this.type = type;
        this.updatedAt = updatedAt;
        this.status = status;
    }
}
