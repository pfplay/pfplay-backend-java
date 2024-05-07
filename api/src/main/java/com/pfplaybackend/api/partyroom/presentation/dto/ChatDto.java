package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.partyroom.enums.MessageType;
import com.pfplaybackend.api.partyroom.model.entity.PartyroomUser;
import com.pfplaybackend.api.partyroom.model.value.PenaltyInfo;
import com.pfplaybackend.api.partyroom.model.value.PromoteInfo;
import lombok.Getter;

@Getter
public class ChatDto {
    private String message;
    private String chatRoomId;
    private MessageType messageType;
    private PartyroomUser fromUser;
    private PartyroomUser toUser;
    private PromoteInfo promoteInfo;
    private PenaltyInfo penaltyInfo;


    public ChatDto(
            PartyroomUser fromUser, String message,
            String chatRoomId, MessageType messageType, PartyroomUser toUser,
            PromoteInfo promoteInfo, PenaltyInfo penaltyInfo
    ) {
        this.fromUser = fromUser;
        this.chatRoomId = chatRoomId;
        this.messageType = messageType;
        this.toUser = toUser;
        this.promoteInfo = promoteInfo;
        this.penaltyInfo = penaltyInfo;
        this.message = message;
    }
}
