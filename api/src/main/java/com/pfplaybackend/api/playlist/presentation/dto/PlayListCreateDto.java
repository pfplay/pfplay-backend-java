package com.pfplaybackend.api.playlist.presentation.dto;

import com.pfplaybackend.api.entity.PlayList;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.playlist.enums.PlayListType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayListCreateDto {
    private final PlayList playList;
    private final Long orderNumber;
    private final String name;
    private final PlayListType type;
    private final User user;

    public PlayList toEntity() {
        return PlayList.builder()
                .orderNumber(orderNumber)
                .name(name)
                .user(user)
                .type(type)
                .build();
    }

}
