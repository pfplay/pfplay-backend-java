package com.pfplaybackend.api.partyroom.repository.impl;

import com.pfplaybackend.api.partyroom.application.dto.*;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.QPartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.QPartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.data.QPlaybackData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.repository.custom.PartyroomRepositoryCustom;
import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import com.pfplaybackend.api.playlist.domain.entity.data.QPlaylistMusicData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.data.QActivityData;
import com.pfplaybackend.api.user.domain.entity.data.QMemberData;
import com.pfplaybackend.api.user.domain.entity.data.QProfileData;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

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

    @Override
    public Optional<ActivePartyroomWithMemberDto> getMyActivePartyroomWithMemberIdByUserId(UserId userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPartymemberData qPartymemberData = QPartymemberData.partymemberData;
        QPartyroomData qPartyroomData = QPartyroomData.partyroomData;

        ActivePartyroomWithMemberDto activePartyroomWithMemberDto = queryFactory
                .select(Projections.constructor(
                        ActivePartyroomWithMemberDto.class,
                        qPartyroomData.id,
                        qPartyroomData.isPlaybackActivated,
                        qPartyroomData.isQueueClosed,
                        qPartyroomData.currentPlaybackId,
                        qPartymemberData.id.as("memberId")
                ))
                .from(qPartymemberData)
                .join(qPartymemberData.partyroomData, qPartyroomData)
                .where(qPartymemberData.userId.eq(userId)
                        .and(qPartymemberData.isActive.eq(true)))
                .fetchOne();

        return Optional.ofNullable(activePartyroomWithMemberDto);
    }

    @Override
    public List<PartyroomWithMemberDto> getMemberDataByPartyroomId() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QPartyroomData partyroom = QPartyroomData.partyroomData;
        QPartymemberData partymember = QPartymemberData.partymemberData;
        QPlaybackData playback = QPlaybackData.playbackData;

        JPQLQuery<Long> memberCountSubquery = JPAExpressions
                .select(partymember.count())
                .from(partymember)
                .where(partymember.partyroomData.id.eq(partyroom.id)
                        .and(partymember.isActive.eq(true))
                        .and(partymember.isBanned.eq(false))
                );

        ConstructorExpression<PlaybackDto> playbackDto = Projections.constructor(PlaybackDto.class,
                playback.id,
                playback.linkId,
                playback.name,
                playback.duration,
                playback.thumbnailImage
        );

        // Fetch partyroom and member data with member count in a single query
        List<Tuple> tuples = queryFactory
                .select(partyroom.id,
                        partyroom.stageType,
                        partyroom.hostId,
                        partyroom.title,
                        partyroom.introduction,
                        partyroom.isPlaybackActivated,
                        partyroom.isQueueClosed,
                        memberCountSubquery,
                        playbackDto,
                        partymember.id,
                        partymember.userId,
                        partymember.authorityTier,
                        partymember.gradeType
                )
                .from(partyroom)
                .leftJoin(partymember)
                .on(partyroom.eq(partymember.partyroomData)
                        .and(partymember.isActive.eq(true))
                        .and(partymember.isBanned.eq(false))
                )
                .leftJoin(playback)
                .on(playback.id.eq(partyroom.currentPlaybackId.id))
                .where(partyroom.isTerminated.eq(false))
                .orderBy(partyroom.id.asc(), partymember.gradeType.asc())
                .fetch();

        // Group member data by partyroom id
        Map<Long, List<PartymemberDto>> membersByPartyroomId = tuples.stream()
                .filter(tuple -> Optional.ofNullable(tuple.get(partymember.id)).isPresent())
                .collect(Collectors.groupingBy(
                        tuple -> Optional.ofNullable(tuple.get(partyroom.id)).orElseThrow(IllegalStateException::new),
                        Collectors.mapping(tuple ->
                                new PartymemberDto(
                                        tuple.get(partymember.id),
                                        tuple.get(partymember.userId),
                                        tuple.get(partymember.authorityTier),
                                        tuple.get(partymember.gradeType)
                                ),
                                Collectors.toList()
                        )
                ));

        return new ArrayList<>(tuples.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(partyroom.id),
                        tuple -> new PartyroomWithMemberDto(
                                tuple.get(partyroom.id),
                                tuple.get(partyroom.stageType),
                                tuple.get(partyroom.hostId),
                                tuple.get(partyroom.title),
                                tuple.get(partyroom.introduction),
                                Boolean.TRUE.equals(tuple.get(partyroom.isPlaybackActivated)),
                                Boolean.TRUE.equals(tuple.get(partyroom.isQueueClosed)),
                                tuple.get(memberCountSubquery),
                                tuple.get(8, PlaybackDto.class),
                                membersByPartyroomId.getOrDefault(tuple.get(partyroom.id), List.of())
                        ),
                        (dto1, dto2) -> dto1
                ))
                .values());
    }
}
