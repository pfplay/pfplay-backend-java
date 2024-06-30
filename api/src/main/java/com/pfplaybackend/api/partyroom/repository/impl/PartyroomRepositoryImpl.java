package com.pfplaybackend.api.partyroom.repository.impl;

import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.PartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.PlaybackDto;
import com.pfplaybackend.api.partyroom.domain.entity.data.QPartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.QPartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.data.QPlaybackData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.repository.custom.PartyroomRepositoryCustom;
import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import com.pfplaybackend.api.playlist.domain.entity.data.QPlaylistMusicData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.data.QActivityData;
import com.pfplaybackend.api.user.domain.entity.data.QMemberData;
import com.pfplaybackend.api.user.domain.entity.data.QProfileData;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

public class PartyroomRepositoryImpl implements PartyroomRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<PartyroomDto> getAllPartyrooms() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPartymemberData qPartymemberData = QPartymemberData.partymemberData;
        QPartyroomData qPartyroomData = QPartyroomData.partyroomData;
        QPlaybackData qPlaybackData = QPlaybackData.playbackData;

        return queryFactory
                .select(Projections.constructor(
                        PartyroomDto.class,
                        qPartyroomData.id,
                        qPartyroomData.stageType,
                        qPartyroomData.hostId,
                        qPartyroomData.title,
                        qPartyroomData.introduction,
                        qPartyroomData.isPlaybackActivated,
                        qPartyroomData.isQueueClosed,
                        qPartymemberData.id.count().as("memberCount"),
                        Projections.constructor(PlaybackDto.class,
                                qPlaybackData.id,
                                qPlaybackData.linkId,
                                qPlaybackData.name,
                                qPlaybackData.duration,
                                qPlaybackData.thumbnailImage
                        )
                ))
                .from(qPartyroomData)
                .leftJoin(qPartymemberData)
                .on(qPartyroomData.eq(qPartymemberData.partyroomData)
                        .and(qPartymemberData.isActive.eq(true))
                        .and(qPartymemberData.isBanned.eq(false))
                )
                .leftJoin(qPlaybackData)
                .on(qPlaybackData.id.eq(qPartyroomData.currentPlaybackId.id))
                .where(qPartyroomData.isTerminated.eq(false))
                .groupBy(qPartyroomData.id)
                .fetch();
    }

    @Override
    public Optional<ActivePartyroomDto> getActivePartyroomByUserId(UserId userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPartymemberData qPartymemberData = QPartymemberData.partymemberData;
        QPartyroomData qPartyroomData = QPartyroomData.partyroomData;

        ActivePartyroomDto activePartyroomDto = queryFactory
                .select(Projections.constructor(
                        ActivePartyroomDto.class,
                        qPartyroomData.id,
                        qPartyroomData.isPlaybackActivated,
                        qPartyroomData.isQueueClosed,
                        qPartyroomData.currentPlaybackId
                ))
                .from(qPartymemberData)
                .join(qPartymemberData.partyroomData, qPartyroomData)
                .where(qPartymemberData.userId.eq(userId)
                        .and(qPartymemberData.isActive.eq(true)))
                .fetchOne();

        return Optional.ofNullable(activePartyroomDto);
    }
}
