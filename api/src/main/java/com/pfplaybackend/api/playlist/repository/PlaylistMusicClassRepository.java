package com.pfplaybackend.api.playlist.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pfplaybackend.api.playlist.model.entity.QPlaylistMusic.playlistMusic;

@RequiredArgsConstructor
@Repository
public class PlaylistMusicClassRepository {
    private final JPAQueryFactory queryFactory;

    public Long deleteByPlayListIds(List<Long> listIds) {
        return queryFactory
                .delete(playlistMusic)
                .where(playlistMusic.playlist.id.in(listIds))
                .execute();
    }

    public Long deleteByIdsAndPlayListId(List<Long> ids, Long playListId) {
        return queryFactory
                .delete(playlistMusic)
                .where(playlistMusic.id.in(ids)
                        .and(playlistMusic.playlist.id.eq(playListId)))
                .execute();
    }
}