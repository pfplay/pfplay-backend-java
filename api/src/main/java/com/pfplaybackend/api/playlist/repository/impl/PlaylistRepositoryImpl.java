package com.pfplaybackend.api.playlist.repository.impl;

import com.pfplaybackend.api.playlist.domain.model.entity.QPlaylistData;
import com.pfplaybackend.api.playlist.domain.model.entity.QPlaylistMusicData;
import com.pfplaybackend.api.playlist.domain.model.enums.PlaylistType;
import com.pfplaybackend.api.playlist.repository.custom.PlaylistRepositoryCustom;
import com.pfplaybackend.api.user.domain.model.value.UserId;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class PlaylistRepositoryImpl implements PlaylistRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Tuple> findByUserId(UserId ownerId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPlaylistData qPlaylistData = QPlaylistData.playlistData;
        QPlaylistMusicData qPlaylistMusicData = QPlaylistMusicData.playlistMusicData;
        return queryFactory
                .select(
                        qPlaylistData.id,
                        qPlaylistData.name,
                        qPlaylistData.orderNumber,
                        qPlaylistData.type,
                        qPlaylistMusicData.id.count().as("count")
                )
                .from(qPlaylistData)
                .leftJoin(qPlaylistMusicData).on(qPlaylistData.id.eq(qPlaylistMusicData.playlistData.id))
                .where(qPlaylistData.ownerId.eq(ownerId))
                .groupBy(qPlaylistData.id)
                .orderBy(qPlaylistData.type.desc(), qPlaylistData.orderNumber.asc())
                .fetch();
    }

    @Override
    public List<Long> findByUserIdAndListIdAndType(UserId ownerId, List<Long> listIds, PlaylistType type){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPlaylistData qPlaylistData = QPlaylistData.playlistData;

        return queryFactory
                .select(
                        qPlaylistData.id
                )
                .from(qPlaylistData)
                .where(qPlaylistData.ownerId.eq(ownerId)
                        .and(qPlaylistData.id.in(listIds))
                        .and(qPlaylistData.type.eq(type)))
                .groupBy(qPlaylistData.id)
                .orderBy(qPlaylistData.type.desc(), qPlaylistData.orderNumber.asc())
                .fetch();
    }

    @Override
    public Long deleteByListIds(List<Long> listIds){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPlaylistData qPlaylistData = QPlaylistData.playlistData;

        return queryFactory
                .delete(qPlaylistData)
                .where(qPlaylistData.id.in(listIds))
                .execute();
    }
}