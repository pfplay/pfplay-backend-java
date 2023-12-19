package com.pfplaybackend.api.partyroom.presentation.dto;


import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.common.enums.Domain;
import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.enums.PartyRoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "파티룸 생성")
public class PartyRoomCreateDto {
    @Schema(description = "이름")
    private final String name;
    @Schema(description = "소개")
    private final String introduce;
    @Schema(description = "도메인")
    private final String domain;
    @Schema(description = "디제잉 제한 시간")
    private final int limit;
    private final User user;
    @Schema(description = "파티룸 타입", implementation = PartyRoomType.class)
    private final PartyRoomType type;
    @Schema(description = "파티룸 상태", implementation = PartyRoomStatus.class)
    private final PartyRoomStatus status;
    private final boolean domainOption;

    public PartyRoom toEntity() {
        return PartyRoom.builder()
                .name(name)
                .user(user)
                .introduce(introduce)
                .domain(domainUrl())
                .djingLimit(limit)
                .type(type)
                .status(status)
                .build();
    }

    public String domainUrl() {
        return Domain.CLIENT.getUrl() + domain;
    }
}
