package com.pfplaybackend.api.playlist.repository.impl;

import com.pfplaybackend.api.playlist.repository.custom.PlaylistMusicRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

import static com.pfplaybackend.api.playlist.domain.model.entity.QPlaylistMusicData.playlistMusicData;

public class PlaylistMusicRepositoryImpl implements PlaylistMusicRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Long deleteByPlayListIds(List<Long> listIds) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        return queryFactory
                .delete(playlistMusicData)
                .where(playlistMusicData.playlistData.id.in(listIds))
                .execute();
    }

    @Override
    public Long deleteByIdsAndPlayListId(List<Long> ids, Long playListId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        return queryFactory
                .delete(playlistMusicData)
                .where(playlistMusicData.id.in(ids)
                        .and(playlistMusicData.playlistData.id.eq(playListId)))
                .execute();
    }
}