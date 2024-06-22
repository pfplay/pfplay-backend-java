package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.partyroom.model.entity.PartyroomUser;
import com.pfplaybackend.api.partyroom.model.value.PromoteInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PromoteDto {
    private String message;
    private PartyroomUser fromUser;
    private PartyroomUser toUser;
    private PromoteInfo promoteInfo;
}
