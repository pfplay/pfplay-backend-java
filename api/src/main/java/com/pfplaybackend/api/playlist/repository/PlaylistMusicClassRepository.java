package com.pfplaybackend.api.playlist.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pfplaybackend.api.playlist.domain.model.entity.QPlaylistMusicData.playlistMusicData;


@RequiredArgsConstructor
@Repository
public class PlaylistMusicClassRepository {
    private final JPAQueryFactory queryFactory;

    public Long deleteByPlayListIds(List<Long> listIds) {
        return queryFactory
                .delete(playlistMusicData)
                .where(playlistMusicData.playlistData.id.in(listIds))
                .execute();
    }

    public Long deleteByIdsAndPlayListId(List<Long> ids, Long playListId) {
        return queryFactory
                .delete(playlistMusicData)
                .where(playlistMusicData.id.in(ids)
                        .and(playlistMusicData.playlistData.id.eq(playListId)))
                .execute();
    }
}