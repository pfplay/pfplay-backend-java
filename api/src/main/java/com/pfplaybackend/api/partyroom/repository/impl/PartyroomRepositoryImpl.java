package com.pfplaybackend.api.partyroom.repository.impl;

import com.pfplaybackend.api.partyroom.application.dto.*;
import com.pfplaybackend.api.partyroom.application.dto.active.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.active.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.CrewDataDto;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.DjDataDto;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.PartyroomDataDto;
import com.pfplaybackend.api.partyroom.domain.entity.data.*;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.repository.custom.PartyroomRepositoryCustom;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.*;
import java.util.stream.Collectors;

public class PartyroomRepositoryImpl implements PartyroomRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<ActivePartyroomDto> getActivePartyroomByUserId(UserId userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPartyroomData qPartyroomData = QPartyroomData.partyroomData;
        QCrewData qCrewData = QCrewData.crewData;

        ActivePartyroomDto activePartyroomDto = queryFactory
                .select(Projections.constructor(
                        ActivePartyroomDto.class,
                        qPartyroomData.id,
                        qPartyroomData.isPlaybackActivated,
                        qPartyroomData.isQueueClosed,
                        qPartyroomData.currentPlaybackId
                ))
                .from(qCrewData)
                .join(qCrewData.partyroomData, qPartyroomData)
                .where(qCrewData.userId.eq(userId)
                        .and(qCrewData.isActive.eq(true)))
                .fetchOne();

        return Optional.ofNullable(activePartyroomDto);
    }

    @Override
    public Optional<ActivePartyroomWithCrewDto> getMyActivePartyroomWithCrewIdByUserId(UserId userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPartyroomData qPartyroomData = QPartyroomData.partyroomData;
        QCrewData qCrewData = QCrewData.crewData;

        ActivePartyroomWithCrewDto activePartyroomWithCrewDto = queryFactory
                .select(Projections.constructor(
                        ActivePartyroomWithCrewDto.class,
                        qPartyroomData.id,
                        qPartyroomData.isPlaybackActivated,
                        qPartyroomData.isQueueClosed,
                        qPartyroomData.currentPlaybackId,
                        qCrewData.id.as("crewId")
                ))
                .from(qCrewData)
                .join(qCrewData.partyroomData, qPartyroomData)
                .where(qCrewData.userId.eq(userId)
                        .and(qCrewData.isActive.eq(true)))
                .fetchOne();

        return Optional.ofNullable(activePartyroomWithCrewDto);
    }

    @Override
    public List<PartyroomWithCrewDto> getCrewDataByPartyroomId() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPartyroomData qPartyroomData = QPartyroomData.partyroomData;
        QCrewData qCrewData = QCrewData.crewData;
        QPlaybackData qPlaybackData = QPlaybackData.playbackData;

        JPQLQuery<Long> crewCountSubquery = JPAExpressions
                .select(qCrewData.count())
                .from(qCrewData)
                .where(qCrewData.partyroomData.id.eq(qPartyroomData.id)
                        .and(qCrewData.isActive.eq(true))
                        .and(qCrewData.isBanned.eq(false))
                );

        ConstructorExpression<PlaybackDto> playbackDto = Projections.constructor(PlaybackDto.class,
                qPlaybackData.id,
                qPlaybackData.linkId,
                qPlaybackData.name,
                qPlaybackData.duration,
                qPlaybackData.thumbnailImage
        );

        // Fetch partyroom and crew data with crew count in a single query
        List<Tuple> tuples = queryFactory
                .select(qPartyroomData.id,
                        qPartyroomData.stageType,
                        qPartyroomData.hostId,
                        qPartyroomData.title,
                        qPartyroomData.introduction,
                        qPartyroomData.isPlaybackActivated,
                        qPartyroomData.isQueueClosed,
                        crewCountSubquery,
                        playbackDto,
                        qCrewData.id,
                        qCrewData.userId,
                        qCrewData.authorityTier,
                        qCrewData.gradeType
                )
                .from(qPartyroomData)
                .leftJoin(qCrewData)
                .on(qPartyroomData.eq(qCrewData.partyroomData)
                        .and(qCrewData.isActive.eq(true))
                        .and(qCrewData.isBanned.eq(false))
                )
                .leftJoin(qPlaybackData)
                .on(qPlaybackData.id.eq(qPartyroomData.currentPlaybackId.id))
                .where(qPartyroomData.isTerminated.eq(false))
                .orderBy(qPartyroomData.id.asc(), qCrewData.gradeType.asc())
                .fetch();

        // Group crew data by partyroom id
        Map<Long, List<CrewDto>> crewsByPartyroomId = tuples.stream()
                .filter(tuple -> Optional.ofNullable(tuple.get(qCrewData.id)).isPresent())
                .collect(Collectors.groupingBy(
                        tuple -> Optional.ofNullable(tuple.get(qPartyroomData.id)).orElseThrow(IllegalStateException::new),
                        Collectors.mapping(tuple ->
                                new CrewDto(
                                        tuple.get(qCrewData.id),
                                        tuple.get(qCrewData.userId),
                                        tuple.get(qCrewData.authorityTier),
                                        tuple.get(qCrewData.gradeType)
                                ),
                                Collectors.toList()
                        )
                ));

        return new ArrayList<>(tuples.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(qPartyroomData.id),
                        tuple -> new PartyroomWithCrewDto(
                                tuple.get(qPartyroomData.id),
                                tuple.get(qPartyroomData.stageType),
                                tuple.get(qPartyroomData.hostId),
                                tuple.get(qPartyroomData.title),
                                tuple.get(qPartyroomData.introduction),
                                Boolean.TRUE.equals(tuple.get(qPartyroomData.isPlaybackActivated)),
                                Boolean.TRUE.equals(tuple.get(qPartyroomData.isQueueClosed)),
                                tuple.get(crewCountSubquery),
                                tuple.get(8, PlaybackDto.class),
                                crewsByPartyroomId.getOrDefault(tuple.get(qPartyroomData.id), List.of())
                        ),
                        (dto1, dto2) -> dto1
                ))
                .values());
    }

    @Override
    public List<PlaybackData> getRecentPlaybackHistory(PartyroomId partyroomId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPartyroomData qPartyroomData = QPartyroomData.partyroomData;
        QPlaybackData qPlaybackData = QPlaybackData.playbackData;

        return queryFactory
                .select(qPlaybackData)
                .from(qPartyroomData, qPlaybackData)
                .where(qPartyroomData.id.eq(partyroomId.getId())
                        .and(qPartyroomData.id.eq(qPlaybackData.partyroomId.id)))
                .orderBy(qPlaybackData.createdAt.desc())
                .limit(20)
                .fetch();
    }

    @Override
    public PartyroomDataDto findPartyroomDto(PartyroomId partyroomId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPartyroomData qPartyroomData = QPartyroomData.partyroomData;
        QCrewData qCrewData = QCrewData.crewData;
        QDjData qDjData = QDjData.djData;

        List<Tuple> result = queryFactory
                .select(qPartyroomData.id, qPartyroomData.title, qPartyroomData.introduction,
                        qCrewData, qDjData)
                .from(qPartyroomData)
                .leftJoin(qPartyroomData.crewDataSet, qCrewData)
                .on(qPartyroomData.eq(qCrewData.partyroomData)
                        .and(qCrewData.isActive.eq(true))
                        .and(qCrewData.isBanned.eq(false))
                )
                .leftJoin(qPartyroomData.djDataSet, qDjData)
                .on(qPartyroomData.eq(qDjData.partyroomData)
                        .and(qDjData.isDeleted.eq(false))
                )
                .where(qPartyroomData.id.eq(partyroomId.getId()))
                .distinct()
                .fetch();

        PartyroomDataDto partyroomDataDto = new PartyroomDataDto(
                result.get(0).get(qPartyroomData.id),
                result.get(0).get(qPartyroomData.title),
                result.get(0).get(qPartyroomData.introduction),
                null, null);

        Map<Long, Set<CrewDataDto>> crewDataMap = result.stream()
                .filter(tuple -> Optional.ofNullable(tuple.get(qCrewData)).isPresent())
                .map(tuple -> {
                    CrewData crewData = tuple.get(qCrewData);
                    assert crewData != null;
                    return CrewDataDto.from(crewData);
                })
                .collect(Collectors.groupingBy(CrewDataDto::getId, Collectors.toSet()));

        Map<Long, Set<DjDataDto>> djDataMap = result.stream()
                .filter(tuple -> Optional.ofNullable(tuple.get(qDjData)).isPresent())
                .map(tuple -> {
                    DjData djData = tuple.get(qDjData);
                    assert djData != null;
                    return DjDataDto.from(djData);
                })
                .collect(Collectors.groupingBy(DjDataDto::getId, Collectors.toSet()));

        partyroomDataDto.setCrewDataSet(crewDataMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet()));
        partyroomDataDto.setDjDataSet(djDataMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet()));
        return partyroomDataDto;
    }


}