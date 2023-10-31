package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.enums.PartyRoomType;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyRoomDto {
    private Long id;
    private String name;
    private UserDto createUser;
    private String introduce;
    private String domain;
    private Integer djingLimit;
    private PartyRoomType type;
    private PartyRoomStatus status;
}
