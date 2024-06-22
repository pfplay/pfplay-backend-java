package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.playlist.domain.entity.domainmodel.Playlist;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.presentation.payload.response.PlaylistResponse;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 플레이리스트 CRUD
 */
@Service
@RequiredArgsConstructor
public class PlaylistQueryService {

    private final PlaylistRepository playlistRepository;

    public List<PlaylistResponse> getPlaylist(UserId ownerId) {
        return null;
//        List<Tuple> result = playlistClassRepository.findByOwnerId(ownerId);
//        List<PlaylistResponse> dtoList = new ArrayList<>();
//        for (Tuple tuple : result) {
//            PlaylistResponse dto = PlaylistResponse.builder()
//                    .id(tuple.get(playlist.id))
//                    .name(tuple.get(playlist.name))
//                    .orderNumber(tuple.get(playlist.orderNumber))
//                    .type(tuple.get(playlist.type))
//                    .count(tuple.get(Expressions.numberPath(Long.class, "count")))
//                    .build();
//            dtoList.add(dto);
//        }
//        return dtoList;
    }

}