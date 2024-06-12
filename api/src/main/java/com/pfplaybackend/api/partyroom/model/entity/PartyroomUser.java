package com.pfplaybackend.api.partyroom.model.entity;

import com.pfplaybackend.api.partyroom.enums.PartyroomGrade;
import com.pfplaybackend.api.user.model.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
public class PartyroomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Embedded
    private UserId userId;
    private String nickname;
    private String chatroomId;
    private PartyroomGrade partyroomGrade;
}
