package com.pfplaybackend.api.playlist.presentation.dto;

import com.pfplaybackend.api.entity.PlayList;
import com.pfplaybackend.api.playlist.enums.PlayListType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayListCreateDto {
    private final PlayList playList;
    private final Long order;
    private final String name;
    private final PlayListType type;

//
//    public PlayList toEntity() {
//        return PlayList.builder()
//                .name(name)
//                .user(user)
//                .introduce(introduce)
//                .domain(Domain.CLIENT.getUrl() + domain)
//                .djingLimit(limit)
//                .type(type)
//                .updatedAt(null)
//                .status(status)
//                .build();
//    }
//
//    public String domainUrl() {
//        return Domain.CLIENT.getUrl() + domain;
//    }
}
