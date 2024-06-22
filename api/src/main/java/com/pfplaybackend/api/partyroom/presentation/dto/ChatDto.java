package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.partyroom.model.entity.PartyroomUser;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatDto {
    private String message;
    @Embedded
    private PartyroomUser fromUser;
}
