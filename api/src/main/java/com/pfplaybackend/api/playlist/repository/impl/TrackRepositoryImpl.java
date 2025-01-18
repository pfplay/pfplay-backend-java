package com.pfplaybackend.api.playlist.repository.impl;

import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import com.pfplaybackend.api.playlist.domain.entity.data.QTrackData;
import com.pfplaybackend.api.playlist.repository.custom.TrackRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class TrackRepositoryImpl implements TrackRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<PlaylistMusicDto> getMusicsWithPagination(Long playlistId, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QTrackData qTrackData = QTrackData.trackData;

        List<PlaylistMusicDto> playlistMusics = queryFactory
                .select(Projections.constructor(PlaylistMusicDto.class,
                        qTrackData.id,
                        qTrackData.linkId,
                        qTrackData.name,
                        qTrackData.orderNumber,
                        qTrackData.duration,
                        qTrackData.thumbnailImage))
                .from(qTrackData)
                .where(qTrackData.playlistData.id.eq(playlistId))
                .orderBy(qTrackData.orderNumber.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(qTrackData.count())
                .from(qTrackData)
                .where(qTrackData.playlistData.id.eq(playlistId))
                .fetchOne();

        long total = count != null ? count : 0;

        return new PageImpl<>(playlistMusics, pageable, total);
    }
}