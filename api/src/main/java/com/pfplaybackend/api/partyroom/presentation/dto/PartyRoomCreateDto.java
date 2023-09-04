package com.pfplaybackend.api.partyroom.presentation.dto;


import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.enums.PartyRoomType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyRoomCreateDto {
    private final String name;
    private final String introduce;
    private final String domain;
    private final int limit;
    private final User user;
    private final PartyRoomType type;
    private final PartyRoomStatus status;

    // @TODO 아래 domain 저장 확인 필요
    private final String domainUrl = "https://pfplay.io/";

    public PartyRoom toEntity() {
        return PartyRoom.builder()
                .name(name)
                .user(user)
                .introduce(introduce)
                .domain(domainUrl + domain)
                .djingLimit(limit)
                .type(type)
                .updatedAt(null)
                .status(status)
                .build();
    }
}
