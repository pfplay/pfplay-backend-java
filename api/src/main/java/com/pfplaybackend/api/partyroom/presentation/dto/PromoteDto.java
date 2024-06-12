package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.partyroom.model.entity.PartyroomUser;
import com.pfplaybackend.api.partyroom.model.value.PromoteInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class PromoteDto implements PartyroomSocketDto{
    private String message;
    private PartyroomUser fromUser;
    private PartyroomUser toUser;
    @Setter
    private String partyroomId;
    private PromoteInfo promoteInfo;
}
