package com.pfplaybackend.api.party.adapter.out.persistence.impl;

import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.crew.CrewDto;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.party.domain.entity.data.*;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.out.persistence.custom.PartyroomRepositoryCustom;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PartyroomRepositoryImpl implements PartyroomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ActivePartyroomDto> getActivePartyroomByUserId(UserId userId) {
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
    public List<PartyroomData> findAllUnusedPartyroomDataByDay(int days) {
        QPartyroomData qPartyroomData = QPartyroomData.partyroomData;

        return queryFactory.select(qPartyroomData)
                .from(qPartyroomData)
                .where(qPartyroomData.updatedAt.before(LocalDateTime.now().minusDays(days)))
                .fetch();
    }
}
