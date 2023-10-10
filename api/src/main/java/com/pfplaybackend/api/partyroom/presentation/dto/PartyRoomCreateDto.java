package com.pfplaybackend.api.partyroom.presentation.dto;


import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.enums.Domain;
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
    private final boolean domainOption;

    public PartyRoom toEntity() {
        return PartyRoom.builder()
                .name(name)
                .user(user)
                .introduce(introduce)
                .domain(Domain.CLIENT.getUrl() + domain)
                .djingLimit(limit)
                .type(type)
                .status(status)
                .build();
    }

    public String domainUrl() {
        return Domain.CLIENT.getUrl() + domain;
    }
}
