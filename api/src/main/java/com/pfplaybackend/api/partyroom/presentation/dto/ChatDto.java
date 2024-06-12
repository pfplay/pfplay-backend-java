package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.partyroom.enums.MessageType;
import com.pfplaybackend.api.partyroom.model.entity.PartyroomUser;
import com.pfplaybackend.api.partyroom.model.value.PenaltyInfo;
import com.pfplaybackend.api.partyroom.model.value.PromoteInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChatDto {
    private String message;
    private MessageType messageType;
    private PartyroomUser fromUser;
    private PartyroomUser toUser;
    private String chatroomId;
    private PromoteInfo promoteInfo;
    private PenaltyInfo penaltyInfo;
}
