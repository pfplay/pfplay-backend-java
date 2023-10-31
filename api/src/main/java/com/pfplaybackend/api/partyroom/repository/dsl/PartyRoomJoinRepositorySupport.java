package com.pfplaybackend.api.partyroom.repository.dsl;

import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.partyroom.presentation.dto.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

import static com.pfplaybackend.api.entity.QPartyPermission.partyPermission;
import static com.pfplaybackend.api.entity.QPartyRoomBan.partyRoomBan;
import static com.pfplaybackend.api.entity.QPartyRoomJoin.partyRoomJoin;
import static com.pfplaybackend.api.entity.QPartyRoom.partyRoom;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PartyRoomJoinRepositorySupport {

    private final JPAQueryFactory query;
    private final ObjectMapperConfig om;

    public Optional<PartyRoomJoinResultDto> findByRoomIdWhereJoinRoom(
            final Long roomId,
            final Long userId
    ) {

        JPAQuery<Tuple> result = query
                .select(
                        partyRoomJoin.id,
                        partyRoomBan,
                        partyRoom,
                        partyPermission
                )
                .from(partyRoomJoin)
                .leftJoin(partyRoomBan).on(partyRoomBan.id.eq(partyRoomJoin.partyRoomBan.id))
                .innerJoin(partyRoom).on(partyRoom.id.eq(partyRoomJoin.partyRoom.id))
                .innerJoin(partyPermission).on(partyPermission.authority.eq(partyRoomJoin.role))
                .where(
                        partyRoomJoin.partyRoom.id.eq(roomId),
                        partyRoomJoin.user.id.eq(userId)
                );

        return result.fetch().stream().map(tuple -> {
                    PartyRoomJoinResultDto build = PartyRoomJoinResultDto.builder()
                            .hasJoined(!Objects.isNull(partyRoomJoin.id))
                            .partyRoom(om.mapper().convertValue(tuple.get(partyRoom), PartyRoomDto.class))
                            .partyPermission(om.mapper().convertValue(tuple.get(partyPermission), PartyPermissionDto.class))
                            .partyRoomBan(om.mapper().convertValue(tuple.get(partyRoomBan), PartyRoomBanDto.class))
                            .build();
                    log.info("findByRoomIdWhereJoinRoom build={}", build);
                    return build;
                }
        ).findFirst();
    }
}
