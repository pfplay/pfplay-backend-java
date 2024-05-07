package com.pfplaybackend.api.partyroom.model.entity;

import com.pfplaybackend.api.partyroom.enums.PartyroomGrade;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class PartyroomUser {
    @Id
    private String uid;
    private String nickname;
    private String chatroomId;
    private PartyroomGrade partyroomGrade;


}
