package com.pfplaybackend.api.playlist.repository.impl;

import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistMusicData;
import com.pfplaybackend.api.playlist.domain.entity.data.QPlaylistMusicData;
import com.pfplaybackend.api.playlist.repository.custom.PlaylistMusicRepositoryCustom;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PlaylistMusicRepositoryImpl implements PlaylistMusicRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<PlaylistMusicDto> getMusicsWithPagination(Long playlistId, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPlaylistMusicData qPlaylistMusicData = QPlaylistMusicData.playlistMusicData;

        List<PlaylistMusicDto> playlistMusics = queryFactory
                .select(Projections.constructor(PlaylistMusicDto.class,
                        qPlaylistMusicData.id,
                        qPlaylistMusicData.playlistData.ownerId,
                        qPlaylistMusicData.orderNumber,
                        qPlaylistMusicData.name,
                        qPlaylistMusicData.duration,
                        qPlaylistMusicData.thumbnailImage))
                .from(qPlaylistMusicData)
                .where(qPlaylistMusicData.playlistData.id.eq(playlistId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(qPlaylistMusicData.count())
                .from(qPlaylistMusicData)
                .where(qPlaylistMusicData.playlistData.id.eq(playlistId))
                .fetchOne();

        long total = count != null ? count : 0;

        return new PageImpl<>(playlistMusics, pageable, total);
    }

    @Override
    public Long deleteByPlayListIds(List<Long> listIds) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPlaylistMusicData playlistMusicData = QPlaylistMusicData.playlistMusicData;

        return queryFactory
                .delete(playlistMusicData)
                .where(playlistMusicData.playlistData.id.in(listIds))
                .execute();
    }

    @Override
    public Long deleteByIdsAndPlayListId(List<Long> ids, Long playListId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPlaylistMusicData playlistMusicData = QPlaylistMusicData.playlistMusicData;

        return queryFactory
                .delete(playlistMusicData)
                .where(playlistMusicData.id.in(ids)
                        .and(playlistMusicData.playlistData.id.eq(playListId)))
                .execute();
    }
}