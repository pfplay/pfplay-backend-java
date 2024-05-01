package com.pfplaybackend.api.playlist.model.domain;

import com.pfplaybackend.api.playlist.model.entity.Playlist;
import com.pfplaybackend.api.playlist.model.enums.PlaylistType;
import com.pfplaybackend.api.user.model.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaylistDomain {
    private final Playlist playList;
    private final Integer orderNumber;
    private final String name;
    private final PlaylistType type;
    private final Member member;

//    public PlayList toEntity() {
//        return PlayList.builder()
//                .orderNumber(orderNumber)
//                .name(name)
//                .user(member)
//                .type(type)
//                .build();
//    }
}
