package com.pfplaybackend.api.partyroom.repository.impl;

import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.domain.entity.data.QPartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.QPartyroomData;
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

import java.util.Optional;

public class PartyroomRepositoryImpl implements PartyroomRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<ActivePartyroomDto> getActivePartyroom(UserId userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QPartymemberData qPartymemberData = QPartymemberData.partymemberData;
        QPartyroomData qPartyroomData = QPartyroomData.partyroomData;

        ActivePartyroomDto activePartyroomDto = queryFactory
                .select(Projections.constructor(
                        ActivePartyroomDto.class,
                        qPartyroomData.id,
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
