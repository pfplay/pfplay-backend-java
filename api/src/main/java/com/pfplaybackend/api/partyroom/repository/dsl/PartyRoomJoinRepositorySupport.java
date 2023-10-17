package com.pfplaybackend.api.partyroom.repository.dsl;

import com.pfplaybackend.api.common.QueryDslConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pfplaybackend.api.entity.QPartyRoomJoin.partyRoomJoin;
import static com.pfplaybackend.api.entity.QPartyRoom.partyRoom;

@Repository
public class PartyRoomJoinRepositorySupport {

    private final JPAQueryFactory query;

    public PartyRoomJoinRepositorySupport(QueryDslConfig queryFactory) {
        query = queryFactory.jpaQueryFactory();
    }

    public List<?> findByRoomIdWhereJoinRoom(final Long roomId) {
        return query
                .from(partyRoomJoin)
                .where(partyRoomJoin.id.eq(roomId))
                .fetch();
    }

}
