package com.pfplaybackend.api.playlist.repository.impl;

import com.pfplaybackend.api.playlist.application.dto.PlaylistSummary;
import com.pfplaybackend.api.playlist.domain.entity.data.QPlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.QTrackData;
import com.pfplaybackend.api.playlist.repository.custom.PlaylistRepositoryCustom;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class PlaylistRepositoryImpl implements PlaylistRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<PlaylistSummary> findAllByUserId(UserId ownerId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPlaylistData qPlaylistData = QPlaylistData.playlistData;
        QTrackData qTrackData = QTrackData.trackData;
        return queryFactory
                .select(Projections.constructor(PlaylistSummary.class,
                                qPlaylistData.id,
                                qPlaylistData.name,
                                qPlaylistData.orderNumber,
                                qPlaylistData.type,
                                qTrackData.id.count().as("memberCount")
                        )
                )
                .from(qPlaylistData)
                .leftJoin(qTrackData).on(qPlaylistData.id.eq(qTrackData.playlistData.id))
                .where(qPlaylistData.ownerId.eq(ownerId))
                .groupBy(qPlaylistData.id)
                .orderBy(qPlaylistData.type.desc(), qPlaylistData.orderNumber.asc())
                .fetch();
    }

    @Override
    public PlaylistSummary findByIdAndUserId(Long playlistId, UserId ownerId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPlaylistData qPlaylistData = QPlaylistData.playlistData;
        QTrackData qTrackData = QTrackData.trackData;
        return queryFactory
                .select(Projections.constructor(PlaylistSummary.class,
                                qPlaylistData.id,
                                qPlaylistData.name,
                                qPlaylistData.orderNumber,
                                qPlaylistData.type,
                                qTrackData.id.count().as("musicCount")
                        )
                )
                .from(qPlaylistData)
                .leftJoin(qTrackData).on(qPlaylistData.id.eq(qTrackData.playlistData.id))
                .where(qPlaylistData.id.eq(playlistId)
                        .and(qPlaylistData.ownerId.eq(ownerId))
                )
                .groupBy(qPlaylistData.id)
                .orderBy(qPlaylistData.type.desc(), qPlaylistData.orderNumber.asc())
                .fetchOne();
    }

    @Override
    public Long deleteByListIds(List<Long> listIds) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPlaylistData qPlaylistData = QPlaylistData.playlistData;

        return queryFactory
                .delete(qPlaylistData)
                .where(qPlaylistData.id.in(listIds))
                .execute();
    }
}